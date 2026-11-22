package com.example.workmanagernewssync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class NewsSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val token = inputData.getString("user_token").orEmpty()
        Log.d("NewsSyncWorker", "Початок синхронізації з токеном: $token")

        // Імітація мережевого запиту
        delay(3000L)

        val outputData = Data.Builder()
            .putString("result", "Успіх: завантажено 15 новин")
            .build()

        return Result.success(outputData)
    }
}