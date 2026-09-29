package com.example.phoneapplication.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.phoneapplication.data.repository.PlantRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class WateringReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: PlantRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val plants = repository.plants.value
        val now = System.currentTimeMillis()
        val day = TimeUnit.DAYS.toMillis(1)

        val duePlants = plants.filter { plant ->
            plant.lastWateredTime + plant.intervalDays * day <= now
        }

        if (duePlants.isNotEmpty()) {
            NotificationHelper.showWateringReminder(
                applicationContext,
                duePlants.map { it.name }
            )
        }

        return Result.success()
    }
}