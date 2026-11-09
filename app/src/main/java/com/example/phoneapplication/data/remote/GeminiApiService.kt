package com.example.phoneapplication.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

@Serializable
data class GeminiRequest(val contents: List<Content>)

@Serializable
data class Content(val parts: List<Part>)

@Serializable
data class Part(val text: String? = null)

@Serializable
data class GeminiResponse(val candidates: List<Candidate>? = null)

@Serializable
data class Candidate(val content: Content? = null)