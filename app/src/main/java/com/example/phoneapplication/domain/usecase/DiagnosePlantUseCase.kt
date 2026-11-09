package com.example.phoneapplication.domain.usecase

import com.example.phoneapplication.data.local.room.dao.DiagnosisDao
import com.example.phoneapplication.data.local.room.entity.DiagnosisEntity
import com.example.phoneapplication.data.model.Plant
import com.example.phoneapplication.domain.llm.LlmException
import com.example.phoneapplication.domain.llm.LlmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class Diagnosis(val symptoms: String, val result: String, val createdAt: Long)

data class DiagnosisResult(val text: String, val fromCache: Boolean)

/**
 * Диагностика растения:
 * 1) смотрим в кэш (БД) — если такой запрос уже был, интернет не нужен;
 * 2) иначе формируем промпт и спрашиваем LLM;
 * 3) чистим ответ и сохраняем в кэш.
 */
class DiagnosePlantUseCase @Inject constructor(
    private val llm: LlmRepository,
    private val dao: DiagnosisDao
) {

    val isAvailable: Boolean get() = llm.isConfigured

    fun history(plantId: Long): Flow<List<Diagnosis>> =
        dao.observeByPlant(plantId).map { list ->
            list.map { Diagnosis(it.symptoms, it.result, it.createdAt) }
        }

    /** Бросает [LlmException], если ответа нет ни от LLM, ни в кэше. */
    suspend operator fun invoke(
        plant: Plant,
        symptoms: String,
        forceRefresh: Boolean = false
    ): DiagnosisResult {
        val clean = symptoms.trim().replace(Regex("\\s+"), " ")
        val key = clean.lowercase()

        val cached = dao.find(plant.id, key)
        if (cached != null && !forceRefresh) {
            return DiagnosisResult(cached.result, fromCache = true)
        }

        try {
            val raw = llm.generate(SYSTEM_PROMPT, buildPrompt(plant, clean))
            val answer = postProcess(raw)

            dao.deleteByKey(plant.id, key)
            dao.insert(
                DiagnosisEntity(
                    plantId = plant.id,
                    symptoms = clean,
                    symptomsKey = key,
                    result = answer,
                    createdAt = System.currentTimeMillis()
                )
            )
            return DiagnosisResult(answer, fromCache = false)
        } catch (e: LlmException) {
            // Graceful degradation: LLM недоступна, но есть старый ответ — показываем его
            if (cached != null) return DiagnosisResult(cached.result, fromCache = true)
            throw e
        }
    }

    private fun buildPrompt(plant: Plant, symptoms: String): String {
        val daysAgo = TimeUnit.MILLISECONDS.toDays(
            System.currentTimeMillis() - plant.lastWateredTime
        ).coerceAtLeast(0)

        return buildString {
            append("У растения «${plant.name}»")
            if (plant.species.isNotBlank()) append(" (${plant.species})")
            append(" такие симптомы: $symptoms\n")
            append("Условия содержания: свет — ${plant.careInfo.light}, ")
            append("влажность — ${plant.careInfo.humidity}, ")
            append("температура — ${plant.careInfo.temperature}.\n")
            append("Последний полив был $daysAgo дн. назад, обычный интервал — ${plant.intervalDays} дн.\n")
            append("Что может быть причиной и что делать?\n")
            append("Ответ строго в формате:\n")
            append("Причина: <1 предложение>\n")
            append("Рекомендация: <1-2 предложения>")
        }
    }

    /** Убираем markdown-символы, лишние пустые строки и ограничиваем длину. */
    private fun postProcess(raw: String): String =
        raw.replace("*", "")
            .replace("#", "")
            .replace("`", "")
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString("\n")
            .take(800)

    private companion object {
        const val SYSTEM_PROMPT =
            "Ты – опытный садовод. Отвечай по-русски, кратко и по делу, " +
                "без markdown, списков и вступлений."
    }
}
