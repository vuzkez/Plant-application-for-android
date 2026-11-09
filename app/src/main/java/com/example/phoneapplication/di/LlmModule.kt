package com.example.phoneapplication.di

import com.example.phoneapplication.BuildConfig
import com.example.phoneapplication.data.remote.llm.OpenRouterApi
import com.example.phoneapplication.data.repository.LlmRepositoryImpl
import com.example.phoneapplication.domain.llm.LlmRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LlmBindModule {
    @Binds
    @Singleton
    abstract fun bindLlmRepository(impl: LlmRepositoryImpl): LlmRepository
}

@Module
@InstallIn(SingletonComponent::class)
object LlmNetworkModule {

    private const val BASE_URL = "https://openrouter.ai/api/"

    @Provides
    @Singleton
    fun provideOpenRouterApi(client: OkHttpClient, json: Json): OpenRouterApi {
        // Заголовок с ключом добавляем ПОСЛЕ логгера, чтобы он не попал в Logcat.
        // HTTP-Referer и X-Title — необязательные заголовки, OpenRouter их рекомендует
        // (влияют на статистику и рейтинг модели, но не на работу).
        val llmClient = client.newBuilder()
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer ${BuildConfig.LLM_API_KEY}")
                    .header("HTTP-Referer", "https://localhost")
                    .header("X-Title", "PhoneApplication")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(llmClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(OpenRouterApi::class.java)
    }
}