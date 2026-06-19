package com.example.jun3.ui.state  // ← Package CORRECTO para esta ubicación

import com.example.jun3.data.Task

sealed class TaskListUiState {
    object Loading : TaskListUiState()
    data class Success(val tasks: List<Task>) : TaskListUiState()
    data class Error(val message: String) : TaskListUiState()
}

sealed class OperationState {
    object Idle : OperationState()
    object Loading : OperationState()
    data class Success(val message: String) : OperationState()
    data class Error(val message: String) : OperationState()
}
