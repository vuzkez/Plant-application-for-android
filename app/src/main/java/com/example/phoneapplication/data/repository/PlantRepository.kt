package com.example.phoneapplication.data.repository

import com.example.phoneapplication.data.model.CareEvent
import com.example.phoneapplication.data.model.Plant
import kotlinx.coroutines.flow.StateFlow

interface PlantRepository {
    val plants: StateFlow<List<Plant>>
    fun waterPlant(plantId: Long, dateMillis: Long)
    fun addPlant(plant: Plant)
    fun addCareEvent(plantId: Long, event: CareEvent)
    fun getPlant(id: Long): Plant?
}