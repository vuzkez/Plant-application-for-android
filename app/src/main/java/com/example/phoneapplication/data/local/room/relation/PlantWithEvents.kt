package com.example.phoneapplication.data.local.room.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.phoneapplication.data.local.room.entity.CareEventEntity
import com.example.phoneapplication.data.local.room.entity.PlantEntity

data class PlantWithEvents(
    @Embedded val plant: PlantEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "plantId"
    )
    val events: List<CareEventEntity>
)