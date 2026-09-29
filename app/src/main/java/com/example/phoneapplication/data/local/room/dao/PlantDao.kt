package com.example.phoneapplication.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.phoneapplication.data.local.room.entity.PlantEntity
import com.example.phoneapplication.data.local.room.relation.PlantWithEvents
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Transaction
    @Query("SELECT * FROM plants ORDER BY id")
    fun observeAllWithEvents(): Flow<List<PlantWithEvents>>

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getById(id: Long): PlantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(plant: PlantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(plants: List<PlantEntity>)

    @Query("UPDATE plants SET lastWateredTime = :time WHERE id = :id")
    suspend fun updateLastWatered(id: Long, time: Long)

    @Query("UPDATE plants SET careGuide = :guide WHERE id = :id")
    suspend fun updateCareGuide(id: Long, guide: String)

    @Query("SELECT COUNT(*) FROM plants")
    suspend fun count(): Int
}