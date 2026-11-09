package com.example.phoneapplication.domain.voice

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Абстракция над SpeechRecognizer (речь -> текст) и TextToSpeech (текст -> речь).
 * UI и ViewModel работают только с этим интерфейсом.
 */
interface VoiceRepository {

    /** Текущее состояние: Idle / Listening / Processing / Error. */
    val state: StateFlow<VoiceUiState>

    /** Каждая распознанная фраза приходит сюда. */
    val recognizedText: SharedFlow<String>

    fun startListening()

    fun stopListening()

    /** Озвучивает текст. */
    fun speak(text: String)

    /** Освобождает SpeechRecognizer и TextToSpeech. Вызывается из ViewModel.onCleared(). */
    fun release()
}
