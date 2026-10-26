package com.example.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

sealed class DownloadState {
    object Idle : DownloadState()
    data class Downloading(val progress: Int) : DownloadState()
    object Success : DownloadState()
    object Canceled : DownloadState()
}

class DownloadViewModel : ViewModel() {

    private val repository = FileRepository()

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> get() = _downloadState

    private var downloadJob: Job? = null

    fun startDownload() {
        if (downloadJob?.isActive == true) return

        downloadJob = viewModelScope.launch {
            repository.downloadFile()
                .onCompletion { cause ->
                    if (cause != null) {
                        _downloadState.value = DownloadState.Canceled
                    } else {
                        // успішне завершення (100%)
                        _downloadState.value = DownloadState.Success
                    }
                }
                .collect { progress ->
                    _downloadState.value = DownloadState.Downloading(progress)
                }
        }
    }

    fun cancelDownload() {
        downloadJob?.cancel()
    }
}