package com.example.phoneapplication.domain.usecase

import com.example.phoneapplication.data.model.CareEvent
import com.example.phoneapplication.data.model.CareType
import com.example.phoneapplication.data.repository.PlantRepository
import com.example.phoneapplication.domain.voice.ParseResult
import com.example.phoneapplication.domain.voice.VoiceCommandParser
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class VoiceCareResult(val message: String, val success: Boolean)

/**
 * Голосовая отметка ухода: «полил кактус» -> запись в журнал -> ответ с датой следующего полива.
 */
class RecordCareByVoiceUseCase @Inject constructor(
    private val repository: PlantRepository
) {

    suspend operator fun invoke(
        text: String,
        now: Long = System.currentTimeMillis()
    ): VoiceCareResult {
        val parsed = VoiceCommandParser.parse(text, repository.plants.value)

        return when (parsed) {
            ParseResult.ActionNotFound -> VoiceCareResult(
                "Не понял, что вы сделали. Скажите: полил, подкормил или пересадил.",
                success = false
            )

            ParseResult.PlantNotFound -> VoiceCareResult(
                "Не нашёл такое растение. Назовите его, например: полил кактус.",
                success = false
            )

            is ParseResult.Ok -> {
                val plant = parsed.plant

                if (parsed.type == CareType.WATERING) {
                    repository.waterPlant(plant.id, now)
                } else {
                    repository.addCareEvent(
                        plant.id,
                        CareEvent(
                            id = 0,
                            plantId = plant.id,
                            type = parsed.type,
                            timestamp = now,
                            note = "${actionName(parsed.type)} (голосом)"
                        )
                    )
                }

                // После полива отсчёт идёт от сейчас, после подкормки и пересадки — от прошлого полива
                val lastWatered = if (parsed.type == CareType.WATERING) now else plant.lastWateredTime
                val nextWatering = lastWatered + TimeUnit.DAYS.toMillis(plant.intervalDays.toLong())

                VoiceCareResult(
                    "Записал: ${actionName(parsed.type)}, ${plant.name}. " +
                        "Следующий полив — ${describeDate(nextWatering, now)}.",
                    success = true
                )
            }
        }
    }

    private fun actionName(type: CareType): String = when (type) {
        CareType.WATERING -> "полив"
        CareType.FERTILIZING -> "подкормка"
        CareType.REPOTTING -> "пересадка"
    }

    private fun describeDate(target: Long, now: Long): String {
        val daysLeft = Math.ceil((target - now) / TimeUnit.DAYS.toMillis(1).toDouble()).toInt()
        return when {
            daysLeft <= 0 -> "уже сегодня"
            daysLeft == 1 -> "завтра"
            else -> SimpleDateFormat("d MMMM", Locale.forLanguageTag("ru")).format(target)
        }
    }
}
