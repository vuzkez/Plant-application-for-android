package com.example.phoneapplication.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phoneapplication.data.model.Plant
import com.example.phoneapplication.data.repository.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    repository: PlantRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val plantId: Long = checkNotNull(savedStateHandle["plantId"])

    val plant: StateFlow<Plant?> = repository.plants
        .map { list -> list.firstOrNull { it.id == plantId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}