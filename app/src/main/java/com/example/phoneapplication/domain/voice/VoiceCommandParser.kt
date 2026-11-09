package com.example.phoneapplication.domain.voice

import com.example.phoneapplication.data.model.CareType
import com.example.phoneapplication.data.model.Plant

sealed interface ParseResult {
    data class Ok(val plant: Plant, val type: CareType) : ParseResult
    data object PlantNotFound : ParseResult
    data object ActionNotFound : ParseResult
}

/**
 * Разбирает фразу вроде «полил кактус» или «подкормил розу»:
 * находит действие и растение. Слова меняют окончания («розу», «розе»),
 * поэтому сравниваем по началу слова.
 */
object VoiceCommandParser {

    private val separators = Regex("[^а-яa-z0-9]+")

    fun parse(text: String, plants: List<Plant>): ParseResult {
        val words = tokenize(text)

        val type = detectType(words) ?: return ParseResult.ActionNotFound
        val plant = findPlant(words, plants) ?: return ParseResult.PlantNotFound
        return ParseResult.Ok(plant, type)
    }

    private fun detectType(words: List<String>): CareType? = when {
        words.any { it.startsWith("подкорм") || it.startsWith("удобр") } -> CareType.FERTILIZING
        words.any { it.startsWith("пересад") } -> CareType.REPOTTING
        words.any { it.startsWith("полил") || it.startsWith("полив") || it.startsWith("напои") } ->
            CareType.WATERING
        else -> null
    }

    private fun findPlant(words: List<String>, plants: List<Plant>): Plant? {
        var best: Plant? = null
        var bestLength = 0

        for (plant in plants) {
            for (token in tokenize(plant.name).filter { it.length >= 3 }) {
                // Отрезаем окончание: «роза» -> «роз», «кактус» -> «какт»
                val stem = if (token.length >= 6) token.dropLast(2) else token.dropLast(1)
                if (stem.length >= 3 && stem.length > bestLength && words.any { it.startsWith(stem) }) {
                    best = plant
                    bestLength = stem.length
                }
            }
        }
        return best
    }

    private fun tokenize(text: String): List<String> =
        text.lowercase()
            .replace('ё', 'е')
            .split(separators)
            .filter { it.isNotEmpty() }
}
