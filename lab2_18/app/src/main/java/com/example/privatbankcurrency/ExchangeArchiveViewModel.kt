package com.example.privatbankcurrency

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.util.UUID

sealed interface SyncStatusUi {
    data object Idle : SyncStatusUi
    data class Enqueued(val text: String = "Очікує мережі / запуску") : SyncStatusUi
    data class Running(val text: String = "Оновлюється...") : SyncStatusUi
    data class Succeeded(val message: String) : SyncStatusUi
    data class Failed(val message: String) : SyncStatusUi
}

class ExchangeArchiveViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    private val dao = ExchangeDatabase.getInstance(appContext).exchangeRateDao()
    private val workManager = WorkManager.getInstance(appContext)

    private val strictFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")
        .withResolverStyle(ResolverStyle.STRICT)
    private val displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    private val _selectedDate = MutableStateFlow(LocalDate.now().format(displayFormatter))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _dateError = MutableStateFlow<String?>(null)
    val dateError: StateFlow<String?> = _dateError.asStateFlow()

    private val _syncStatus = MutableStateFlow<SyncStatusUi>(SyncStatusUi.Idle)
    val syncStatus: StateFlow<SyncStatusUi> = _syncStatus.asStateFlow()

    private var observedWorkLiveData: LiveData<WorkInfo?>? = null
    private var workObserver: ((WorkInfo?) -> Unit)? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val rates: StateFlow<List<ExchangeRateEntity>> = _selectedDate
        .flatMapLatest { date -> dao.observeRates(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onDateTextChanged(newDate: String) {
        _selectedDate.value = newDate
        _dateError.value = null
    }

    fun onDatePickedFromCalendar(date: LocalDate) {
        _selectedDate.value = date.format(displayFormatter)
        _dateError.value = null
    }

    fun refreshRatesForSelectedDate() {
        val dateToSync = _selectedDate.value.trim()
        val validationError = validateDate(dateToSync)
        if (validationError != null) {
            _dateError.value = validationError
            return
        }

        _dateError.value = null
        _syncStatus.value = SyncStatusUi.Enqueued()

        val workId = ExchangeSyncScheduler.enqueueOneTimeSync(appContext, dateToSync)
        observeWorkId(workId)
    }

    private fun observeWorkId(workId: UUID) {
        workObserver?.let { observer ->
            observedWorkLiveData?.removeObserver(observer)
        }

        val liveData = workManager.getWorkInfoByIdLiveData(workId)
        val observer: (WorkInfo?) -> Unit = { workInfo ->
            if (workInfo != null) {
                val outputMessage = workInfo.outputData.getString(ExchangeSyncScheduler.KEY_MESSAGE)
                _syncStatus.value = when (workInfo.state) {
                    WorkInfo.State.ENQUEUED,
                    WorkInfo.State.BLOCKED -> SyncStatusUi.Enqueued("Очікує мережі / запуску")

                    WorkInfo.State.RUNNING -> SyncStatusUi.Running("Оновлюється...")

                    WorkInfo.State.SUCCEEDED -> SyncStatusUi.Succeeded(
                        outputMessage ?: "Дані оновлено"
                    )

                    WorkInfo.State.FAILED -> SyncStatusUi.Failed(
                        outputMessage ?: "Не вдалося оновити курси"
                    )

                    WorkInfo.State.CANCELLED -> SyncStatusUi.Failed("Оновлення скасовано")
                }
            }
        }

        observedWorkLiveData = liveData
        workObserver = observer
        liveData.observeForever(observer)
    }

    private fun validateDate(dateStr: String): String? {
        if (dateStr.isBlank()) return "Введіть дату у форматі дд.мм.рррр"

        val parsed = try {
            LocalDate.parse(dateStr, strictFormatter)
        } catch (_: DateTimeParseException) {
            return "Некоректна дата. Використовуйте формат дд.мм.рррр"
        }

        val today = LocalDate.now()
        val minDate = today.minusYears(4)

        return when {
            parsed.isAfter(today) -> "Не можна запитувати майбутню дату"
            parsed.isBefore(minDate) -> "Архів доступний лише за останні 4 роки"
            else -> null
        }
    }

    override fun onCleared() {
        workObserver?.let { observer ->
            observedWorkLiveData?.removeObserver(observer)
        }
        super.onCleared()
    }
}