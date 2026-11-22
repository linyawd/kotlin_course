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
        setContent {
            PrivatBankCurrencyTheme {
                val viewModel: ExchangeArchiveViewModel = viewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                ExchangeArchiveScreen(
                    state = state,
                    onDateTextChanged = viewModel::onDateTextChanged,
                    onDatePickedFromCalendar = viewModel::onDatePickedFromCalendar,
                    onLoadClick = viewModel::loadRates
                )
            }
        }
    }
}