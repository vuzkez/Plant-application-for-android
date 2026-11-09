package com.example.phoneapplication.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Кэш ответов LLM: симптомы -> диагноз. Нужен, чтобы ответ можно было показать без интернета. */
@Entity(tableName = "diagnoses")
data class DiagnosisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val symptoms: String,      // как ввёл пользователь
    val symptomsKey: String,   // нижний регистр, для поиска в кэше
    val result: String,
    val createdAt: Long
)
