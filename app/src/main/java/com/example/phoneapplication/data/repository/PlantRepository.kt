package com.example.phoneapplication.data.repository

import com.example.phoneapplication.data.model.CareEvent
import com.example.phoneapplication.data.model.Plant
import kotlinx.coroutines.flow.StateFlow

interface PlantRepository {
    val plants: StateFlow<List<Plant>>
    suspend fun waterPlant(plantId: Long, dateMillis: Long)
    suspend fun addPlant(plant: Plant)
    suspend fun addCareEvent(plantId: Long, event: CareEvent)
    fun getPlant(id: Long): Plant?
    suspend fun seedIfEmpty()
}