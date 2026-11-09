package com.example.phoneapplication.data.repository

import kotlinx.coroutines.flow.Flow

interface LlmRepository {
    suspend fun diagnosePlant(symptoms: String): Flow<LlmUiState>
}

sealed interface LlmUiState {
    object Idle : LlmUiState
    object Loading : LlmUiState
    data class Success(val response: String) : LlmUiState
    data class Error(val message: String) : LlmUiState
}