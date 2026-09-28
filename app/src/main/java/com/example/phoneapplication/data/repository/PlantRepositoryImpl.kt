package com.example.phoneapplication.data.repository

import com.example.phoneapplication.data.local.PlantLocalDataSource
import com.example.phoneapplication.data.model.CareEvent
import com.example.phoneapplication.data.model.CareType
import com.example.phoneapplication.data.model.Plant
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantRepositoryImpl @Inject constructor(
    private val local: PlantLocalDataSource
) : PlantRepository {

    override val plants: StateFlow<List<Plant>> = local.plants

    override fun waterPlant(plantId: Long, dateMillis: Long) {
        val plant = local.plants.value.firstOrNull { it.id == plantId } ?: return
        local.update(plant.copy(lastWateredTime = dateMillis))
        local.addEvent(
            plantId,
            CareEvent(
                id = System.nanoTime(),
                plantId = plantId,
                type = CareType.WATERING,
                timestamp = dateMillis,
                note = "Полив"
            )
        )
    }

    override fun addPlant(plant: Plant) = local.add(plant)

    override fun addCareEvent(plantId: Long, event: CareEvent) =
        local.addEvent(plantId, event)

    override fun getPlant(id: Long): Plant? =
        local.plants.value.firstOrNull { it.id == id }
}