package com.example.jun3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.jun3.data.Task
import com.example.jun3.data.TaskStatus

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        // Datos de ejemplo en memoria
        _tasks.value = listOf(
            Task(1, "Tarea 1", "Descripción 1", status = TaskStatus.TODO),
            Task(2, "Tarea 2", "Descripción 2", status = TaskStatus.IN_PROGRESS),
            Task(3, "Tarea 3", "Descripción 3", status = TaskStatus.DONE)
        )
    }

    fun addTask(title: String, description: String = "") {
        viewModelScope.launch {
            val currentTasks = _tasks.value.toMutableList()
            val newId = (currentTasks.maxByOrNull { it.id }?.id ?: 0) + 1
            val newTask = Task(
                id = newId,
                title = title,
                description = description,
                status = TaskStatus.TODO
            )
            currentTasks.add(newTask)
            _tasks.value = currentTasks
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            val currentTasks = _tasks.value.toMutableList()
            currentTasks.remove(task)
            _tasks.value = currentTasks
        }
    }

    fun updateTaskStatus(task: Task, newStatus: TaskStatus) {
        viewModelScope.launch {
            val currentTasks = _tasks.value.toMutableList()
            val index = currentTasks.indexOfFirst { it.id == task.id }
            if (index != -1) {
                val updatedTask = task.copy(status = newStatus)
                currentTasks[index] = updatedTask
                _tasks.value = currentTasks
            }
        }
    }
}