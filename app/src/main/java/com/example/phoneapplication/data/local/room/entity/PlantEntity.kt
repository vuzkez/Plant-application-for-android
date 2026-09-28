package com.example.phoneapplication.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val species: String,
    val lastWateredTime: Long,
    val intervalDays: Int,
    val light: String,
    val humidity: String,
    val temperature: String,
    val description: String
)