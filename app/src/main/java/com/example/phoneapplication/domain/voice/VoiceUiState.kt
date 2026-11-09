package com.example.phoneapplication.domain.voice

/** Состояние голосового ввода. */
sealed interface VoiceUiState {
    /** Ничего не происходит. */
    data object Idle : VoiceUiState

    /** Микрофон включён, пользователь говорит. */
    data object Listening : VoiceUiState

    /** Речь закончилась, идёт распознавание. */
    data object Processing : VoiceUiState

    /** Что-то пошло не так (нет разрешения, микрофон занят, язык не поддерживается и т.д.). */
    data class Error(val message: String) : VoiceUiState
}
