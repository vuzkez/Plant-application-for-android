package com.example.phoneapplication.data.remote.llm

import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val systemInstruction: GeminiContent? = null,
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null
)

@Serializable
data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart> = emptyList()
)

@Serializable
data class GeminiPart(
    val text: String? = null
)

/** Параметры генерации: temperature — "творчество", topP — отсечение редких слов. */
@Serializable
data class GeminiGenerationConfig(
    val temperature: Double? = null,
    val topP: Double? = null,
    val maxOutputTokens: Int? = null
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)
