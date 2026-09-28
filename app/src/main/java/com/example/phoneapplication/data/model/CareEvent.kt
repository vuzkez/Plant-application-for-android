package com.example.phoneapplication.data.model

data class CareEvent(
    val id: Long,
    val plantId: Long,
    val type: CareType,
    val timestamp: Long,
    val note: String = ""
)