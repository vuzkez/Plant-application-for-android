package com.example.phoneapplication.data.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "care_events",
    foreignKeys = [
        ForeignKey(
            entity = PlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plantId")]
)
data class CareEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val type: String,
    val timestamp: Long,
    val note: String
)