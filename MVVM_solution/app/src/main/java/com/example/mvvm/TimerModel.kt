package com.example.mvvm

import android.os.CountDownTimer

class TimerModel {

    private var countDownTimer: CountDownTimer? = null
    private var isRunning = false

    fun start(
        seconds: Long = 30L,
        onTick: (Long) -> Unit,
        onFinish: () -> Unit
    ) {
        stop()

        val totalMillis = seconds * 1000L
        val intervalMillis = 1000L

        isRunning = true

        countDownTimer = object : CountDownTimer(totalMillis, intervalMillis) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingSeconds = millisUntilFinished / 1000L
                onTick(remainingSeconds)
            }

            override fun onFinish() {
                isRunning = false
                onFinish()
            }
        }.start()
    }

    fun stop() {
        countDownTimer?.cancel()
        countDownTimer = null
        isRunning = false
    }

    fun isRunning(): Boolean = isRunning
}