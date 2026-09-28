package com.example.phoneapplication.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phoneapplication.data.local.realm.PlantRealmRepository
import com.example.phoneapplication.data.local.realm.objects.FertilizerType
import com.example.phoneapplication.data.local.realm.objects.PlantFamily
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    realmRepository: PlantRealmRepository
) : ViewModel() {

    val families: StateFlow<List<PlantFamily>> =
        realmRepository.observeFamilies()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val fertilizers: StateFlow<List<FertilizerType>> =
        realmRepository.observeFertilizers()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}