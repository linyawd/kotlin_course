package com.example.waterreminder

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.waterreminder.ui.theme.WaterReminderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaterReminderTheme {
                val viewModel: WaterReminderViewModel = viewModel()
                val reminderCount by viewModel.reminderCount.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WaterReminderScreen(
                        reminderCount = reminderCount,
                        onStartReminder = viewModel::startReminder,
                        onStopReminder = viewModel::stopReminder,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}