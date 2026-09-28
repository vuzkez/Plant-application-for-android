package com.example.phoneapplication

import android.app.Application
import com.example.phoneapplication.data.local.realm.PlantRealmRepository
import com.example.phoneapplication.data.repository.PlantRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class PlantApplication : Application() {

    @Inject lateinit var plantRepository: PlantRepository
    @Inject lateinit var realmRepository: PlantRealmRepository

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            plantRepository.seedIfEmpty()
            realmRepository.seedIfEmpty()
        }
    }
}