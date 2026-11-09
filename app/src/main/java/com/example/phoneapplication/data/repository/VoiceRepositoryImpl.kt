package com.example.phoneapplication.data.repository

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.core.content.ContextCompat
import com.example.phoneapplication.domain.voice.VoiceRepository
import com.example.phoneapplication.domain.voice.VoiceUiState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject

/**
 * Работает с SpeechRecognizer и TextToSpeech.
 * Все методы вызываются с главного потока (из ViewModel), так требует SpeechRecognizer.
 */
class VoiceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : VoiceRepository {

    private val _state = MutableStateFlow<VoiceUiState>(VoiceUiState.Idle)
    override val state: StateFlow<VoiceUiState> = _state.asStateFlow()

    private val _recognizedText = MutableSharedFlow<String>(extraBufferCapacity = 1)
    override val recognizedText: SharedFlow<String> = _recognizedText.asSharedFlow()

    private var recognizer: SpeechRecognizer? = null

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var ttsProblem: String? = null
    private var pendingSpeech: String? = null

    init {
        initTts()
    }

    // ---------- Речь -> текст ----------

    override fun startListening() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            _state.value = VoiceUiState.Error(
                "Нет доступа к микрофону. Разреши его в настройках приложения."
            )
            return
        }

        val speechRecognizer = getOrCreateRecognizer() ?: return

        tts?.stop() // чтобы микрофон не услышал нашу же озвучку

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        _state.value = VoiceUiState.Listening
        speechRecognizer.startListening(intent)
    }

    override fun stopListening() {
        recognizer?.stopListening()
    }

    private fun getOrCreateRecognizer(): SpeechRecognizer? {
        recognizer?.let { return it }

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _state.value = VoiceUiState.Error(
                "На телефоне нет службы распознавания речи (нужны сервисы Google)."
            )
            return null
        }
        return SpeechRecognizer.createSpeechRecognizer(context).also {
            it.setRecognitionListener(listener)
            recognizer = it
        }
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            _state.value = VoiceUiState.Listening
        }

        override fun onBeginningOfSpeech() {
            _state.value = VoiceUiState.Listening
        }

        override fun onEndOfSpeech() {
            _state.value = VoiceUiState.Processing
        }

        override fun onResults(results: Bundle?) {
            val text = results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                ?.trim()

            if (text.isNullOrEmpty()) {
                _state.value = VoiceUiState.Error("Не удалось разобрать речь. Попробуй ещё раз.")
            } else {
                _state.value = VoiceUiState.Idle
                _recognizedText.tryEmit(text)
            }
        }

        override fun onError(error: Int) {
            _state.value = VoiceUiState.Error(errorMessage(error))
        }

        override fun onRmsChanged(rmsdB: Float) = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onPartialResults(partialResults: Bundle?) = Unit
        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    }

    private fun errorMessage(code: Int): String = when (code) {
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
            "Нет доступа к микрофону. Разреши его в настройках приложения."
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
            "Микрофон занят другим приложением. Закрой его и попробуй снова."
        SpeechRecognizer.ERROR_NO_MATCH ->
            "Не расслышал. Скажи ещё раз, например: «полил кактус»."
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
            "Ничего не услышал. Нажми кнопку и говори сразу."
        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
            "Для распознавания речи нужен интернет."
        SpeechRecognizer.ERROR_AUDIO ->
            "Ошибка записи звука. Проверь микрофон."
        SpeechRecognizer.ERROR_SERVER ->
            "Сервис распознавания речи сейчас недоступен."
        12, 13 -> // ERROR_LANGUAGE_NOT_SUPPORTED / ERROR_LANGUAGE_UNAVAILABLE (Android 12+)
            "Русский язык для распознавания не поддерживается или не скачан."
        else -> "Ошибка распознавания (код $code). Попробуй ещё раз."
    }

    // ---------- Текст -> речь ----------

    private fun initTts() {
        tts = TextToSpeech(context) { status ->
            if (status != TextToSpeech.SUCCESS) {
                ttsProblem = "Синтез речи недоступен на этом устройстве."
                return@TextToSpeech
            }
            val result = tts?.setLanguage(Locale.forLanguageTag("ru-RU"))
            if (result == null ||
                result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                ttsProblem = "Русский голос не установлен. " +
                    "Скачай его: Настройки → Язык и ввод → Синтез речи."
                return@TextToSpeech
            }
            ttsReady = true
            pendingSpeech?.let {
                pendingSpeech = null
                speak(it)
            }
        }
    }

    override fun speak(text: String) {
        if (text.isBlank()) return

        ttsProblem?.let {
            _state.value = VoiceUiState.Error(it)
            return
        }
        val engine = tts
        if (!ttsReady || engine == null) {
            pendingSpeech = text // голос ещё загружается, озвучим сразу после инициализации
            return
        }

        // У TextToSpeech есть лимит длины текста, поэтому режем на куски
        val maxLength = TextToSpeech.getMaxSpeechInputLength() - 1
        text.chunked(maxLength).forEachIndexed { index, part ->
            val mode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            engine.speak(part, mode, null, "voice-$index")
        }
    }

    // ---------- Освобождение ресурсов ----------

    override fun release() {
        recognizer?.destroy()
        recognizer = null

        tts?.stop()
        tts?.shutdown()
        tts = null
        ttsReady = false
    }
}
