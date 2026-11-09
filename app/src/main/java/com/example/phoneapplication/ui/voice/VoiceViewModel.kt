package com.example.phoneapplication.ui.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phoneapplication.domain.usecase.RecordCareByVoiceUseCase
import com.example.phoneapplication.domain.voice.VoiceRepository
import com.example.phoneapplication.domain.voice.VoiceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Что показываем на экране: что услышали и что ответили. */
data class VoiceScreenState(
    val recognized: String = "",
    val reply: String = "",
    val replySuccess: Boolean = true
)

@HiltViewModel
class VoiceViewModel @Inject constructor(
    private val voice: VoiceRepository,
    private val recordCare: RecordCareByVoiceUseCase
) : ViewModel() {

    /** Idle / Listening / Processing / Error */
    val voiceState: StateFlow<VoiceUiState> = voice.state

    private val _screen = MutableStateFlow(VoiceScreenState())
    val screen: StateFlow<VoiceScreenState> = _screen.asStateFlow()

    init {
        // Поток данных: микрофон -> репозиторий -> ViewModel -> use case -> состояние -> UI
        viewModelScope.launch {
            voice.recognizedText.collect { text -> onRecognized(text) }
        }
    }

    /** Единственное событие от UI: нажали на микрофон. */
    fun onMicClick() {
        if (voice.state.value is VoiceUiState.Listening) {
            voice.stopListening()
        } else {
            voice.startListening()
        }
    }

    private suspend fun onRecognized(text: String) {
        _screen.update { it.copy(recognized = text, reply = "") }

        val result = recordCare(text)

        _screen.update { it.copy(reply = result.message, replySuccess = result.success) }
        voice.speak(result.message)
    }

    override fun onCleared() {
        voice.release() // destroy() у SpeechRecognizer и shutdown() у TextToSpeech
        super.onCleared()
    }
}
