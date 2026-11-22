package com.example.foregroundtimerservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.foregroundtimerservice.ui.theme.ForegroundTimerServiceTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val _timerSeconds = MutableStateFlow(0)
    private var boundService: TimerForegroundService? = null
    private var isBound = false
    private var collectJob: Job? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerForegroundService.LocalBinder
            val myService = binder.getService()
            boundService = myService
            isBound = true

            collectJob?.cancel()
            collectJob = lifecycleScope.launch {
                myService.getTimerFlow().collect { seconds ->
                    _timerSeconds.value = seconds
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            boundService = null
            isBound = false
            collectJob?.cancel()
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, TimerForegroundService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            collectJob?.cancel()
            unbindService(connection)
            boundService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForegroundTimerServiceTheme {
                val seconds by _timerSeconds.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TimerScreen(
                        seconds = seconds,
                        onStopClick = {
                            boundService?.stopTimer()
                            stopService(Intent(this@MainActivity, TimerForegroundService::class.java))
                            _timerSeconds.value = 0
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}