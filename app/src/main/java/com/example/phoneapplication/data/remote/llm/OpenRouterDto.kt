package com.example.phoneapplication.data.remote.llm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenRouterRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val temperature: Double? = null,
    @SerialName("top_p") val topP: Double? = null,
    @SerialName("max_tokens") val maxTokens: Int? = null
)

@Serializable
data class OpenRouterMessage(
    val role: String,
    val content: String
)

@Serializable
data class OpenRouterResponse(
    val choices: List<OpenRouterChoice>? = null,
    val error: OpenRouterError? = null
)

@Serializable
data class OpenRouterChoice(
    val message: OpenRouterMessage? = null,
    @SerialName("finish_reason") val finishReason: String? = null
)

@Serializable
data class OpenRouterError(
    val message: String? = null,
    val code: Int? = null
)