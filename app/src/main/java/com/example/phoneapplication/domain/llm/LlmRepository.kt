package com.example.phoneapplication.domain.llm

/**
 * Абстракция над LLM. Остальное приложение ничего не знает про Gemini и Retrofit.
 * Чтобы заменить LLM (например, на локальную модель), достаточно написать другую реализацию.
 */
interface LlmRepository {

    /** Задан ли ключ API. Если нет — функция недоступна. */
    val isConfigured: Boolean

    /** Отправляет запрос и возвращает текст ответа. При ошибке бросает [LlmException]. */
    suspend fun generate(systemPrompt: String, userPrompt: String): String
}

enum class LlmError {
    NO_API_KEY,
    NO_INTERNET,
    BAD_KEY,
    BAD_MODEL,
    RATE_LIMIT,
    SERVER,
    EMPTY_ANSWER,
    UNKNOWN
}

class LlmException(val error: LlmError, cause: Throwable? = null) : Exception(error.name, cause)
