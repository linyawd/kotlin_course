package com.example.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel : ViewModel() {

    private val timerModel = TimerModel()

    private val _timerLiveData = MutableLiveData<Long>()
    val timerLiveData: LiveData<Long> get() = _timerLiveData

    private val _isTimerRunning = MutableLiveData<Boolean>(false)
    val isTimerRunning: LiveData<Boolean> get() = _isTimerRunning

    fun startTimer(userSeconds: Long? = null) {
        val secondsToRun = if (userSeconds == null || userSeconds <= 0) 30L else userSeconds

        _isTimerRunning.value = true
        _timerLiveData.value = secondsToRun

        timerModel.start(
            seconds = secondsToRun,
            onTick = { secondsLeft ->
                _timerLiveData.postValue(secondsLeft)
            },
            onFinish = {
                _timerLiveData.postValue(0L)
                _isTimerRunning.postValue(false)
            }
        )
    }

    fun resetTimer(defaultSeconds: Long = 30L) {
        timerModel.stop()
        _isTimerRunning.value = false
        _timerLiveData.value = defaultSeconds
    }

    override fun onCleared() {
        super.onCleared()
        timerModel.stop()
    }
}