package com.example.test
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.test.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: DownloadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.btnDownload.setOnClickListener {
            viewModel.startDownload()
        }

        binding.btnCancel.setOnClickListener {
            viewModel.cancelDownload()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.downloadState.collect { state ->
                    when (state) {
                        is DownloadState.Idle -> {
                            binding.tvStatus.text = "Натисніть скачати"
                            binding.progressBar.progress = 0
                            binding.btnDownload.isEnabled = true
                            binding.btnCancel.isEnabled = false
                        }

                        is DownloadState.Downloading -> {
                            binding.tvStatus.text = "Завантаження: ${state.progress}%"
                            binding.progressBar.progress = state.progress
                            binding.btnDownload.isEnabled = false
                            binding.btnCancel.isEnabled = true
                        }

                        is DownloadState.Success -> {
                            binding.tvStatus.text = "Файл успішно скачано!"
                            binding.progressBar.progress = 100
                            binding.btnDownload.isEnabled = true
                            binding.btnCancel.isEnabled = false
                        }

                        is DownloadState.Canceled -> {
                            binding.tvStatus.text = "Завантаження скасовано"
                            binding.btnDownload.isEnabled = true
                            binding.btnCancel.isEnabled = false
                        }
                    }
                }
            }
        }
    }
}