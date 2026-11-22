package com.example.waterreminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

class WaterReminderViewModel(
    application: Application
) : AndroidViewModel(application) {

    init {
        WaterPrefs.init(application.applicationContext)
    }

    val reminderCount: StateFlow<Int> = WaterPrefs.countFlow

    fun startReminder(intervalMinutes: Long) {
        // Мінімальний інтервал PeriodicWorkRequest у WorkManager — 15 хвилин
        val safeInterval = if (intervalMinutes < 15L) 15L else intervalMinutes

        val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            safeInterval,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(getApplication<Application>().applicationContext)
            .enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }

    fun stopReminder() {
        WorkManager.getInstance(getApplication<Application>().applicationContext)
            .cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    companion object {
        const val UNIQUE_WORK_NAME = "water_reminder"
    }
}