package com.example.phoneapplication.util

import com.example.phoneapplication.data.model.Plant
import com.example.phoneapplication.data.model.WateringStatus
import java.util.concurrent.TimeUnit

object WateringCalculator {
    fun getStatus(plant: Plant, referenceDate: Long): WateringStatus {
        val intervalMillis = TimeUnit.DAYS.toMillis(plant.intervalDays.toLong())
        val nextWatering = plant.lastWateredTime + intervalMillis
        val warningThreshold = nextWatering - TimeUnit.DAYS.toMillis(2)
        return when {
            referenceDate >= nextWatering -> WateringStatus.OVERDUE
            referenceDate >= warningThreshold -> WateringStatus.WARNING
            else -> WateringStatus.OK
        }
    }
}