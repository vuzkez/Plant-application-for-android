package com.example.phoneapplication.data.model

data class Plant(
    val id: Long,
    val name: String,
    val species: String,
    val lastWateredTime: Long,
    val intervalDays: Int,
    val careInfo: PlantCareInfo = PlantCareInfo(),
    val careGuide: String = "",
    val history: List<CareEvent> = emptyList()
)