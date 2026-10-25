package com.example.mvvm

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mvvm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.timerLiveData.observe(this) { seconds ->
            binding.tvTimer.text = seconds.toString()
        }

        viewModel.isTimerRunning.observe(this) { isRunning ->
            binding.btnStart.isEnabled = !isRunning
            binding.etInitialTime.isEnabled = !isRunning
        }
    }

    private fun setupListeners() {
        binding.btnStart.setOnClickListener {
            startTimer()
        }

        binding.btnReset.setOnClickListener {
            Log.d(TAG, "Reset timer clicked")
            viewModel.resetTimer()
        }
    }

    private fun startTimer() {
        Log.d(TAG, "startTimer called via ViewModel")

        val inputText = binding.etInitialTime.text.toString()
        val userSeconds = inputText.toLongOrNull()

        viewModel.startTimer(userSeconds)
    }

    companion object {
        val TAG = "XXXX"
    }
}