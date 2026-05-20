package com.example.jun3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jun3.data.Task
import com.example.jun3.data.TaskStatus
import com.example.jun3.ui.state.TaskListUiState
import com.example.jun3.ui.state.OperationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    // Estado de la lista de tareas
    private val _taskListState = MutableStateFlow<TaskListUiState>(TaskListUiState.Loading)
    val taskListState: StateFlow<TaskListUiState> = _taskListState.asStateFlow()

    // Estado de operaciones (agregar, actualizar, eliminar)
    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    // Datos en memoria (temporal - luego reemplazaremos con Room)
    private var tasks = mutableListOf(
        Task(1, "Estudiar para el examen", "Repasar matemáticas y física", TaskStatus.TODO),
        Task(2, "Hacer la compra", "Comprar frutas y verduras", TaskStatus.IN_PROGRESS),
        Task(3, "Llamar al médico", "Pedir cita para chequeo", TaskStatus.DONE)
    )

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            try {
                // Simular carga de datos
                _taskListState.value = TaskListUiState.Success(tasks.toList())
            } catch (e: Exception) {
                _taskListState.value = TaskListUiState.Error("Error al cargar tareas: ${e.message}")
            }
        }
    }

    fun addTask(title: String, description: String = "") {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val newTask = Task(
                    id = (tasks.maxOfOrNull { it.id } ?: 0) + 1,
                    title = title,
                    description = description,
                    status = TaskStatus.TODO
                )
                tasks.add(newTask)
                _taskListState.value = TaskListUiState.Success(tasks.toList())
                _operationState.value = OperationState.Success("Tarea agregada correctamente")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Error al agregar tarea: ${e.message}")
            }
        }
    }

    fun updateTaskStatus(taskId: Long, newStatus: TaskStatus) {
        viewModelScope.launch {
            try {
                val taskIndex = tasks.indexOfFirst { it.id == taskId }
                if (taskIndex != -1) {
                    tasks[taskIndex] = tasks[taskIndex].copy(status = newStatus)
                    _taskListState.value = TaskListUiState.Success(tasks.toList())
                }
            } catch (e: Exception) {
                // Podríamos mostrar un error aquí si es necesario
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                tasks.removeAll { it.id == taskId }
                _taskListState.value = TaskListUiState.Success(tasks.toList())
                _operationState.value = OperationState.Success("Tarea eliminada")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Error al eliminar tarea: ${e.message}")
            }
        }
    }

    // Resetear estado de operación
    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}
// timestamp: 2026-05-20 18:35:35
