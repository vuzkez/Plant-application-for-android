package com.example.phoneapplication.data.local

import com.example.phoneapplication.data.model.CareEvent
import com.example.phoneapplication.data.model.CareType
import com.example.phoneapplication.data.model.Plant
import com.example.phoneapplication.data.model.PlantCareInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantLocalDataSource @Inject constructor() {

    private val _plants = MutableStateFlow(seed())
    val plants: StateFlow<List<Plant>> = _plants.asStateFlow()

    fun update(plant: Plant) {
        _plants.value = _plants.value.map { if (it.id == plant.id) plant else it }
    }

    fun add(plant: Plant) { _plants.value = _plants.value + plant }

    fun addEvent(plantId: Long, event: CareEvent) {
        _plants.value = _plants.value.map {
            if (it.id == plantId) it.copy(history = it.history + event) else it
        }
    }

    private fun seed(): List<Plant> {
        val now = System.currentTimeMillis()
        val day = TimeUnit.DAYS.toMillis(1)
        return listOf(
            Plant(1, "Кактус", "Эхинокактус", now - 12 * day, 10,
                PlantCareInfo("Прямое солнце", "Низкая", "+20…+30 °C", "Кактус семейства кактусовых."),
                listOf(CareEvent(1, 1, CareType.WATERING, now - 12 * day, "Обильный полив"))),
            Plant(2, "Орхидея", "Фаленопсис", now - 15 * day, 14,
                PlantCareInfo("Яркий рассеянный", "Высокая (60–80%)", "+18…+25 °C", "Эпифит, любит влажность."),
                listOf(CareEvent(2, 2, CareType.WATERING, now - 15 * day, "Погружной полив"))),
            Plant(3, "Роза", "Чайная", now - 4 * day, 3,
                PlantCareInfo("Прямое солнце", "Средняя", "+18…+22 °C", "Требует регулярной обрезки.")),
            Plant(4, "Фикус", "Бенджамина", now - 5 * day, 7,
                PlantCareInfo("Рассеянный", "Средняя", "+18…+24 °C", "Не любит сквозняки.")),
            Plant(5, "Герань", "Плющелистная", now - 8 * day, 10,
                PlantCareInfo("Солнечное место", "Низкая", "+15…+22 °C", "Обильно цветёт.")),
            Plant(6, "Папоротник", "Нефролепис", now - 1 * day, 7,
                PlantCareInfo("Полутень", "Высокая", "+16…+22 °C", "Опрыскивать регулярно."))
        )
    }
}