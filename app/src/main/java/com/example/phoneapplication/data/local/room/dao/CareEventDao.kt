package com.example.phoneapplication.data.local.room.dao

import androidx.room.*
import com.example.phoneapplication.data.local.room.entity.CareEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CareEventDao {

    @Query("SELECT * FROM care_events WHERE plantId = :plantId ORDER BY timestamp DESC")
    fun observeForPlant(plantId: Long): Flow<List<CareEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: CareEventEntity)

    @Query("DELETE FROM care_events WHERE plantId = :plantId")
    suspend fun deleteForPlant(plantId: Long)
}