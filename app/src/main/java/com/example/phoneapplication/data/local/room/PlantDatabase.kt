package com.example.phoneapplication.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.phoneapplication.data.local.room.dao.CareEventDao
import com.example.phoneapplication.data.local.room.dao.DiagnosisDao
import com.example.phoneapplication.data.local.room.dao.PlantDao
import com.example.phoneapplication.data.local.room.entity.CareEventEntity
import com.example.phoneapplication.data.local.room.entity.DiagnosisEntity
import com.example.phoneapplication.data.local.room.entity.PlantEntity

@Database(
    entities = [PlantEntity::class, CareEventEntity::class, DiagnosisEntity::class],
    version = 3,
    exportSchema = false
)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun careEventDao(): CareEventDao
    abstract fun diagnosisDao(): DiagnosisDao
}
