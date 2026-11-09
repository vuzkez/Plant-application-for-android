package com.example.phoneapplication.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val TREFLE_BASE_URL = "https://trefle.io/api/v1/"
    private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    // --- Retrofit для Trefle ---
    @Provides
    @Singleton
    @Named("trefle")
    fun provideTrefleRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(TREFLE_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun providePlantApi(@Named("trefle") retrofit: Retrofit): PlantApi =
        retrofit.create(PlantApi::class.java)

    // --- Retrofit для Gemini ---
    @Provides
    @Singleton
    @Named("gemini")
    fun provideGeminiRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(GEMINI_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideGeminiApiService(@Named("gemini") retrofit: Retrofit): GeminiApiService =
        retrofit.create(GeminiApiService::class.java)
}