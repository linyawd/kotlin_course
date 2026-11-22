package com.example.privatbankcurrency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.privatbankcurrency.ui.theme.PrivatBankCurrencyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Реєструємо періодичне оновлення поточної дати
        ExchangeSyncScheduler.enqueuePeriodicSync(applicationContext)

        setContent {
            PrivatBankCurrencyTheme {
                val viewModel: ExchangeArchiveViewModel = viewModel()
                val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
                val dateError by viewModel.dateError.collectAsStateWithLifecycle()
                val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
                val rates by viewModel.rates.collectAsStateWithLifecycle()

                ExchangeArchiveScreen(
                    selectedDate = selectedDate,
                    dateError = dateError,
                    syncStatus = syncStatus,
                    rates = rates,
                    onDateTextChanged = viewModel::onDateTextChanged,
                    onDatePickedFromCalendar = viewModel::onDatePickedFromCalendar,
                    onRefreshClick = viewModel::refreshRatesForSelectedDate
                )
            }
        }
    }
}