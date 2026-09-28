package com.example.phoneapplication.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phoneapplication.data.model.Plant
import com.example.phoneapplication.data.repository.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: PlantRepository
) : ViewModel() {

    private val _recognized = MutableStateFlow("")
    val recognized: StateFlow<String> = _recognized.asStateFlow()

    fun onTextRecognized(text: String) {
        if (text.isNotBlank()) _recognized.value = text.trim()
    }

    fun savePlant(name: String) {
        viewModelScope.launch {
            val id = System.currentTimeMillis() % 100000
            repository.addPlant(
                Plant(
                    id = id,
                    name = name,
                    species = "Уточнить",
                    lastWateredTime = System.currentTimeMillis(),
                    intervalDays = 7
                )
            )
        }
    }
}