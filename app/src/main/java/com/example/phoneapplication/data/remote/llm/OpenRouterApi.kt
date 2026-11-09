package com.example.phoneapplication.data.remote.llm

import retrofit2.http.Body
import retrofit2.http.POST

interface OpenRouterApi {
    @POST("v1/chat/completions")
    suspend fun chatCompletions(@Body request: OpenRouterRequest): OpenRouterResponse
}