package com.example.phoneapplication.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.phoneapplication.data.local.room.entity.DiagnosisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosisDao {

    @Query("SELECT * FROM diagnoses WHERE plantId = :plantId AND symptomsKey = :key ORDER BY createdAt DESC LIMIT 1")
    suspend fun find(plantId: Long, key: String): DiagnosisEntity?

    @Query("SELECT * FROM diagnoses WHERE plantId = :plantId ORDER BY createdAt DESC")
    fun observeByPlant(plantId: Long): Flow<List<DiagnosisEntity>>

    @Insert
    suspend fun insert(entity: DiagnosisEntity)

    @Query("DELETE FROM diagnoses WHERE plantId = :plantId AND symptomsKey = :key")
    suspend fun deleteByKey(plantId: Long, key: String)
}
