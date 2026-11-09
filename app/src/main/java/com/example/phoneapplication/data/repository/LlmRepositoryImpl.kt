package com.example.phoneapplication.data.repository

import com.example.phoneapplication.BuildConfig
import com.example.phoneapplication.data.remote.llm.OpenRouterApi
import com.example.phoneapplication.data.remote.llm.OpenRouterMessage
import com.example.phoneapplication.data.remote.llm.OpenRouterRequest
import com.example.phoneapplication.domain.llm.LlmError
import com.example.phoneapplication.domain.llm.LlmException
import com.example.phoneapplication.domain.llm.LlmRepository
import com.example.phoneapplication.util.NetworkChecker
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/** LLM через OpenRouter (OpenAI-совместимый API). */
@Singleton
class LlmRepositoryImpl @Inject constructor(
    private val api: OpenRouterApi,
    private val network: NetworkChecker
) : LlmRepository {

    override val isConfigured: Boolean
        get() = BuildConfig.LLM_API_KEY.isNotBlank()

    override suspend fun generate(systemPrompt: String, userPrompt: String): String {
        if (!isConfigured) throw LlmException(LlmError.NO_API_KEY)
        if (!network.isOnline()) throw LlmException(LlmError.NO_INTERNET)

        val request = OpenRouterRequest(
            model = BuildConfig.LLM_MODEL,
            messages = listOf(
                OpenRouterMessage(role = "system", content = systemPrompt),
                OpenRouterMessage(role = "user",   content = userPrompt)
            ),
            temperature = 0.4,
            topP = 0.9,
            maxTokens = 1024
        )

        try {
            val response = api.chatCompletions(request)

            // OpenRouter иногда отдаёт 200 с ошибкой в теле (лимиты, нет кредитов)
            response.error?.let { throw LlmException(LlmError.UNKNOWN) }

            val text = response.choices
                ?.firstOrNull()
                ?.message
                ?.content
                ?.trim()

            if (text.isNullOrBlank()) throw LlmException(LlmError.EMPTY_ANSWER)
            return text
        } catch (e: LlmException) {
            throw e
        } catch (e: HttpException) {
            throw LlmException(
                when (e.code()) {
                    400, 401, 403 -> LlmError.BAD_KEY
                    402           -> LlmError.RATE_LIMIT  // Payment Required
                    404           -> LlmError.BAD_MODEL
                    429           -> LlmError.RATE_LIMIT
                    in 500..599   -> LlmError.SERVER
                    else          -> LlmError.UNKNOWN
                },
                e
            )
        } catch (e: IOException) {
            throw LlmException(LlmError.NO_INTERNET, e)
        } catch (e: SerializationException) {
            throw LlmException(LlmError.UNKNOWN, e)
        }
    }
}