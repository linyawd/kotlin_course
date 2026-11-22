package com.example.privatbankcurrency

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.UUID
import java.util.concurrent.TimeUnit

object ExchangeSyncScheduler {
    const val PERIODIC_WORK_NAME = "exchange_rates_periodic"
    const val KEY_DATE = "date"
    const val KEY_MESSAGE = "message"

    private fun networkConstraints(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun enqueuePeriodicSync(context: Context) {
        val request = PeriodicWorkRequestBuilder<ExchangeRatesSyncWorker>(
            1L,
            TimeUnit.HOURS
        )
            .setConstraints(networkConstraints())
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun enqueueOneTimeSync(context: Context, date: String): UUID {
        val request = OneTimeWorkRequestBuilder<ExchangeRatesSyncWorker>()
            .setConstraints(networkConstraints())
            .setInputData(workDataOf(KEY_DATE to date))
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            "exchange_rates_$date",
            ExistingWorkPolicy.KEEP,
            request
        )

        return request.id
    }
}