package com.example.jun3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jun3.data.Task
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FocusViewModel : ViewModel() {

    // Estado del temporizador
    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    // Iniciar sesión de enfoque
    fun startFocusSession(task: Task) {
        // Si ya hay una sesión activa, la detenemos
        stopFocusSession()

        _uiState.value = FocusUiState(
            isActive = true,
            currentTask = task,
            elapsedSeconds = 0,
            isPaused = false
        )

        // Iniciar el temporizador
        timerJob = viewModelScope.launch {
            while (_uiState.value.isActive && !_uiState.value.isPaused) {
                delay(1000) // Actualizar cada segundo
                _uiState.value = _uiState.value.copy(
                    elapsedSeconds = _uiState.value.elapsedSeconds + 1
                )
            }
        }
    }

    // Pausar/Reanudar
    fun togglePause() {
        if (!_uiState.value.isActive) return

        val currentState = _uiState.value
        _uiState.value = currentState.copy(isPaused = !currentState.isPaused)

        if (!_uiState.value.isPaused) {
            // Reanudar el temporizador
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

    // Detener sesión de enfoque
    fun stopFocusSession() {
        timerJob?.cancel()
        timerJob = null
        _uiState.value = FocusUiState()
    }

    // Formatear tiempo (mm:ss)
    fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}

// Estado de la UI para el enfoque
data class FocusUiState(
    val isActive: Boolean = false,
    val currentTask: Task? = null,
    val elapsedSeconds: Long = 0,
    val isPaused: Boolean = false
)