package com.example.privatbankcurrency

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

sealed interface ExchangeUiState {
    data object Idle : ExchangeUiState
    data object Loading : ExchangeUiState
    data class Success(
        val responseDate: String,
        val rates: List<ExchangeRateItem>
    ) : ExchangeUiState
    data class Error(val message: String) : ExchangeUiState
}

data class ExchangeScreenState(
    val dateInput: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
    val dateValidationError: String? = null,
    val uiState: ExchangeUiState = ExchangeUiState.Idle
) {
    val isLoading: Boolean
        get() = uiState is ExchangeUiState.Loading
}

class ExchangeArchiveViewModel : ViewModel() {

    private val strictFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")
        .withResolverStyle(ResolverStyle.STRICT)

    private val _state = MutableStateFlow(ExchangeScreenState())
    val state: StateFlow<ExchangeScreenState> = _state.asStateFlow()

    private var activeCall: Call<ExchangeArchiveResponse>? = null

    fun onDateTextChanged(newDate: String) {
        if (_state.value.isLoading) return
        _state.update {
            it.copy(
                dateInput = newDate,
                dateValidationError = null
            )
        }
    }

    fun onDatePickedFromCalendar(date: LocalDate) {
        if (_state.value.isLoading) return
        val formatted = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        _state.update {
            it.copy(
                dateInput = formatted,
                dateValidationError = null
            )
        }
    }

    fun loadRates() {
        if (_state.value.isLoading) return

        val rawDate = _state.value.dateInput.trim()
        val validationError = validateDate(rawDate)
        if (validationError != null) {
            _state.update { it.copy(dateValidationError = validationError) }
            return
        }

        activeCall?.cancel()
        _state.update {
            it.copy(
                dateValidationError = null,
                uiState = ExchangeUiState.Loading
            )
        }

        Log.d("PrivatBankAPI", "Запит курсів на дату: $rawDate")

        val call = RetrofitClient.api.getExchangeRates(rawDate)
        activeCall = call

        call.enqueue(object : Callback<ExchangeArchiveResponse> {
            override fun onResponse(
                call: Call<ExchangeArchiveResponse>,
                response: Response<ExchangeArchiveResponse>
            ) {
                activeCall = null
                if (!response.isSuccessful) {
                    _state.update {
                        it.copy(uiState = ExchangeUiState.Error("Помилка сервера: HTTP ${response.code()}"))
                    }
                    return
                }

                val body = response.body()
                if (body == null) {
                    _state.update {
                        it.copy(uiState = ExchangeUiState.Error("Сервер повернув порожню відповідь."))
                    }
                    return
                }

                val validRates = body.exchangeRate.filter { !it.currency.isNullOrBlank() }
                val actualDate = body.date?.takeIf { it.isNotBlank() } ?: rawDate

                _state.update {
                    it.copy(
                        uiState = ExchangeUiState.Success(
                            responseDate = actualDate,
                            rates = validRates
                        )
                    )
                }
            }

            override fun onFailure(call: Call<ExchangeArchiveResponse>, t: Throwable) {
                if (call.isCanceled) return
                activeCall = null
                Log.e("PrivatBankAPI", "Помилка запиту", t)
                _state.update {
                    it.copy(
                        uiState = ExchangeUiState.Error(
                            "Не вдалося завантажити курси. Перевірте з'єднання та спробуйте знову."
                        )
                    )
                }
            }
        })
    }

    private fun validateDate(dateStr: String): String? {
        if (dateStr.isBlank()) {
            return "Введіть дату у форматі дд.мм.рррр"
        }

        val parsedDate = try {
            LocalDate.parse(dateStr, strictFormatter)
        } catch (_: DateTimeParseException) {
            return "Некоректна дата. Використовуйте формат дд.мм.рррр (наприклад, 01.11.2026)"
        }

        val today = LocalDate.now()
        val minAllowedDate = today.minusYears(4)

        return when {
            parsedDate.isAfter(today) -> "Не можна запитувати майбутню дату"
            parsedDate.isBefore(minAllowedDate) -> "Архів доступний лише за останні 4 роки (з ${minAllowedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))})"
            else -> null
        }
    }

    override fun onCleared() {
        activeCall?.cancel()
        activeCall = null
        super.onCleared()
    }
}