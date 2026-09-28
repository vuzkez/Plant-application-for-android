package com.example.phoneapplication.data.repository

import com.example.phoneapplication.data.local.room.dao.CareEventDao
import com.example.phoneapplication.data.local.room.dao.PlantDao
import com.example.phoneapplication.data.local.room.entity.CareEventEntity
import com.example.phoneapplication.data.local.room.entity.PlantEntity
import com.example.phoneapplication.data.local.room.relation.PlantWithEvents
import com.example.phoneapplication.data.model.CareEvent
import com.example.phoneapplication.data.model.CareType
import com.example.phoneapplication.data.model.Plant
import com.example.phoneapplication.data.model.PlantCareInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao,
    private val careEventDao: CareEventDao
) : PlantRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    override val plants: StateFlow<List<Plant>> = plantDao.observeAllWithEvents()
        .map { list -> list.map { it.toDomain() } }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override suspend fun waterPlant(plantId: Long, dateMillis: Long) {
        plantDao.updateLastWatered(plantId, dateMillis)
        careEventDao.insert(
            CareEventEntity(
                plantId = plantId,
                type = CareType.WATERING.name,
                timestamp = dateMillis,
                note = "Полив"
            )
        )
    }

    override suspend fun addPlant(plant: Plant) {
        plantDao.upsert(plant.toEntity())
    }

    override suspend fun addCareEvent(plantId: Long, event: CareEvent) {
        careEventDao.insert(
            CareEventEntity(
                plantId = plantId,
                type = event.type.name,
                timestamp = event.timestamp,
                note = event.note
            )
        )
    }

    override fun getPlant(id: Long): Plant? = runBlocking {
        plantDao.getById(id)?.let { entity ->
            Plant(
                id = entity.id,
                name = entity.name,
                species = entity.species,
                lastWateredTime = entity.lastWateredTime,
                intervalDays = entity.intervalDays,
                careInfo = PlantCareInfo(
                    entity.light, entity.humidity,
                    entity.temperature, entity.description
                )
            )
        }
    }

    override suspend fun seedIfEmpty() {
        if (plantDao.count() > 0) return
        val now = System.currentTimeMillis()
        val day = TimeUnit.DAYS.toMillis(1)
        val seed = listOf(
            PlantEntity(1, "Кактус", "Эхинокактус", now - 12 * day, 10,
                "Прямое солнце", "Низкая", "+20…+30 °C", "Кактус семейства кактусовых."),
            PlantEntity(2, "Орхидея", "Фаленопсис", now - 15 * day, 14,
                "Яркий рассеянный", "Высокая (60–80%)", "+18…+25 °C", "Эпифит, любит влажность."),
            PlantEntity(3, "Роза", "Чайная", now - 4 * day, 3,
                "Прямое солнце", "Средняя", "+18…+22 °C", "Требует регулярной обрезки."),
            PlantEntity(4, "Фикус", "Бенджамина", now - 5 * day, 7,
                "Рассеянный", "Средняя", "+18…+24 °C", "Не любит сквозняки."),
            PlantEntity(5, "Герань", "Плющелистная", now - 8 * day, 10,
                "Солнечное место", "Низкая", "+15…+22 °C", "Обильно цветёт."),
            PlantEntity(6, "Папоротник", "Нефролепис", now - 1 * day, 7,
                "Полутень", "Высокая", "+16…+22 °C", "Опрыскивать регулярно.")
        )
        plantDao.upsertAll(seed)

        careEventDao.insert(
            CareEventEntity(plantId = 1, type = "WATERING",
                timestamp = now - 12 * day, note = "Обильный полив")
        )
        careEventDao.insert(
            CareEventEntity(plantId = 2, type = "WATERING",
                timestamp = now - 15 * day, note = "Погружной полив")
        )
    }

    private fun PlantWithEvents.toDomain() = Plant(
        id = plant.id,
        name = plant.name,
        species = plant.species,
        lastWateredTime = plant.lastWateredTime,
        intervalDays = plant.intervalDays,
        careInfo = PlantCareInfo(
            plant.light, plant.humidity,
            plant.temperature, plant.description
        ),
        history = events.map {
            CareEvent(
                id = it.id,
                plantId = it.plantId,
                type = CareType.valueOf(it.type),
                timestamp = it.timestamp,
                note = it.note
            )
        }
    )

    private fun Plant.toEntity() = PlantEntity(
        id = id,
        name = name,
        species = species,
        lastWateredTime = lastWateredTime,
        intervalDays = intervalDays,
        light = careInfo.light,
        humidity = careInfo.humidity,
        temperature = careInfo.temperature,
        description = careInfo.description
    )
}