package com.example.workmanagernewssync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.workmanagernewssync.ui.theme.WorkManagerNewsSyncTheme
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val _statusText = MutableStateFlow("Очікує...")
    private val _periodicMessage = MutableStateFlow<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkManagerNewsSyncTheme {
                val status by _statusText.collectAsStateWithLifecycle()
                val periodicMsg by _periodicMessage.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NewsSyncScreen(
                        statusText = status,
                        periodicMessage = periodicMsg,
                        onSyncNowClick = { enqueueOneTimeSync() },
                        onEnableNightSyncClick = { enqueueNightSync() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun enqueueOneTimeSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putString("user_token", "abc123")
            .build()

        val request = OneTimeWorkRequestBuilder<NewsSyncWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(request)

        workManager.getWorkInfoByIdLiveData(request.id)
            .observe(this) { workInfo ->
                if (workInfo != null) {
                    _statusText.value = when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> "Очікує..."
                        WorkInfo.State.RUNNING -> "Виконується..."
                        WorkInfo.State.SUCCEEDED -> {
                            val resultMsg = workInfo.outputData.getString("result") ?: "Успіх!"
                            resultMsg
                        }
                        WorkInfo.State.FAILED -> "Помилка виконання"
                        WorkInfo.State.CANCELLED -> "Скасовано"
                        else -> workInfo.state.name
                    }
                }
            }
    }

    private fun enqueueNightSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()

        val request = PeriodicWorkRequestBuilder<NewsSyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "night_news_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

        _periodicMessage.value = "Нічну синхронізацію активовано (Wi-Fi + зарядка, кожні 15 хв)"
    }
}