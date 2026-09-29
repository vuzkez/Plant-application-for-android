package com.example.phoneapplication

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.phoneapplication.data.local.realm.PlantRealmRepository
import com.example.phoneapplication.data.repository.PlantRepository
import com.example.phoneapplication.worker.WorkScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class PlantApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var plantRepository: PlantRepository
    @Inject lateinit var realmRepository: PlantRealmRepository

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        WorkScheduler.schedule(this)

        CoroutineScope(Dispatchers.IO).launch {
            plantRepository.seedIfEmpty()
            realmRepository.seedIfEmpty()
        }
    }
}