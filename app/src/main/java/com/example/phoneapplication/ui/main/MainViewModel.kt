package com.example.phoneapplication.ui.main

import androidx.lifecycle.ViewModel
import com.example.phoneapplication.data.model.Plant
import com.example.phoneapplication.data.repository.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PlantRepository
) : ViewModel() {

    val plants: StateFlow<List<Plant>> = repository.plants

    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    fun onDateSelected(millis: Long) { _selectedDate.value = millis }

    fun waterPlant(plantId: Long) {
        repository.waterPlant(plantId, _selectedDate.value)
    }
}