package com.example.phoneapplication.di

import com.example.phoneapplication.data.repository.VoiceRepositoryImpl
import com.example.phoneapplication.domain.voice.VoiceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Репозиторий живёт столько же, сколько ViewModel.
 * Когда ViewModel уничтожается, вызывается release() — и SpeechRecognizer с TextToSpeech освобождаются.
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class VoiceModule {
    @Binds
    @ViewModelScoped
    abstract fun bindVoiceRepository(impl: VoiceRepositoryImpl): VoiceRepository
}
