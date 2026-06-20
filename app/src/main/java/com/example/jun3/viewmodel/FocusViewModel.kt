package com.example.jun3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.jun3.data.Task
import com.example.jun3.utils.PreferenceHelper
import com.example.jun3.workers.FocusReminderWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class FocusViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val prefs = PreferenceHelper(application)
    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var currentTaskId: Long = 0

    // Observador del ciclo de vida de la app
    init {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    scheduleFocusReminder()
                }
                Lifecycle.Event.ON_RESUME -> {
                    cancelFocusReminder()
                }
                else -> { /* No hacer nada */ }
            }
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
    }

    fun startFocusSession(task: Task) {
        stopFocusSession()

        currentTaskId = task.id

        _uiState.value = FocusUiState(
            isActive = true,
            currentTask = task,
            elapsedSeconds = 0,
            isPaused = false
        )

        timerJob = viewModelScope.launch {
            while (_uiState.value.isActive && !_uiState.value.isPaused) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    elapsedSeconds = _uiState.value.elapsedSeconds + 1
                )
            }
        }
    }

    fun togglePause() {
        if (!_uiState.value.isActive) return

        val currentState = _uiState.value
        _uiState.value = currentState.copy(isPaused = !currentState.isPaused)

        if (!_uiState.value.isPaused) {
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                while (_uiState.value.isActive && !_uiState.value.isPaused) {
                    delay(1000)
                    _uiState.value = _uiState.value.copy(
                        elapsedSeconds = _uiState.value.elapsedSeconds + 1
                    )
                }
            }
        }
    }

    fun stopFocusSession() {
        timerJob?.cancel()
        timerJob = null

        val currentState = _uiState.value
        if (currentState.isActive && currentState.currentTask != null) {
            //  Guardar el tiempo enfocado en SharedPreferences
            prefs.saveFocusTime(currentState.elapsedSeconds)
            // room despues
        }

        _uiState.value = FocusUiState()
        cancelFocusReminder()
    }

    private fun scheduleFocusReminder() {
        val currentState = _uiState.value
        if (!currentState.isActive || currentState.currentTask == null) return

        val taskTitle = currentState.currentTask!!.title

        val workRequest = OneTimeWorkRequestBuilder<FocusReminderWorker>()
            .setInitialDelay(2, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .setInputData(
                workDataOf("task_title" to taskTitle)
            )
            .build()

        WorkManager.getInstance(getApplication()).enqueue(workRequest)
    }

    private fun cancelFocusReminder() {
        WorkManager.getInstance(getApplication()).cancelAllWork()
        val notificationHelper = com.example.jun3.notifications.NotificationHelper(getApplication())
        notificationHelper.cancelNotifications()
    }

    fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(FocusViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return FocusViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}

data class FocusUiState(
    val isActive: Boolean = false,
    val currentTask: Task? = null,
    val elapsedSeconds: Long = 0,
    val isPaused: Boolean = false
)