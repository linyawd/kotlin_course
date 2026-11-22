package com.example.systemeventsjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.systemeventsjournal.ui.theme.SystemEventsJournalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SystemEventsJournalTheme {
                val viewModel: BroadcastViewModel = viewModel()
                val events by viewModel.events.collectAsStateWithLifecycle()

                EventsJournalScreen(
                    events = events,
                    onDeleteEvent = viewModel::deleteEvent,
                    onClearAll = viewModel::clearAll
                )
            }
        }
    }
}