package com.example.phoneapplication.ui.diagnosis

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phoneapplication.data.model.Plant
import com.example.phoneapplication.data.repository.PlantRepository
import com.example.phoneapplication.domain.llm.LlmError
import com.example.phoneapplication.domain.llm.LlmException
import com.example.phoneapplication.domain.usecase.Diagnosis
import com.example.phoneapplication.domain.usecase.DiagnosePlantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Состояние экрана диагностики. UI только читает его, а события шлёт во ViewModel (UDF). */
sealed interface DiagnosisUiState {
    data object Idle : DiagnosisUiState
    data object Loading : DiagnosisUiState
    data class Success(val text: String, val fromCache: Boolean) : DiagnosisUiState
    data class Error(val message: String) : DiagnosisUiState
}

@HiltViewModel
class DiagnosisViewModel @Inject constructor(
    private val repository: PlantRepository,
    private val diagnosePlant: DiagnosePlantUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val plantId: Long = checkNotNull(savedStateHandle["plantId"])

    val plant: StateFlow<Plant?> = repository.plants
        .map { list -> list.firstOrNull { it.id == plantId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val history: StateFlow<List<Diagnosis>> = diagnosePlant.history(plantId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val isLlmConfigured: Boolean = diagnosePlant.isAvailable

    private val _state = MutableStateFlow<DiagnosisUiState>(DiagnosisUiState.Idle)
    val state: StateFlow<DiagnosisUiState> = _state.asStateFlow()

    private var job: Job? = null

    fun diagnose(symptoms: String, forceRefresh: Boolean = false) {
        val current = repository.plants.value.firstOrNull { it.id == plantId } ?: return
        if (symptoms.isBlank() || _state.value is DiagnosisUiState.Loading) return

        job?.cancel()
        job = viewModelScope.launch {
            _state.value = DiagnosisUiState.Loading
            _state.value = try {
                val result = diagnosePlant(current, symptoms, forceRefresh)
                DiagnosisUiState.Success(result.text, result.fromCache)
            } catch (e: LlmException) {
                DiagnosisUiState.Error(e.error.toMessage())
            }
        }
    }

    private fun LlmError.toMessage(): String = when (this) {
        LlmError.NO_API_KEY -> "Не задан ключ LLM_API_KEY в local.properties"
        LlmError.NO_INTERNET -> "Нет интернета. Диагностика работает только онлайн, " +
            "но уже полученные ответы сохраняются в кэше."
        LlmError.BAD_KEY -> "Ключ API не подошёл. Проверь LLM_API_KEY."
        LlmError.BAD_MODEL -> "Модель не найдена. Проверь LLM_MODEL в local.properties."
        LlmError.RATE_LIMIT -> "Превышен бесплатный лимит запросов. Попробуй через минуту."
        LlmError.SERVER -> "Сервер LLM сейчас недоступен. Попробуй позже."
        LlmError.EMPTY_ANSWER -> "LLM не дала ответ. Опиши симптомы по-другому."
        LlmError.UNKNOWN -> "Что-то пошло не так. Попробуй ещё раз."
    }
}
