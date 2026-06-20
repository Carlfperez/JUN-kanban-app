package com.example.jun3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.jun3.data.Task
import com.example.jun3.data.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskListViewModel : ViewModel() {
    private val _tasks = MutableStateFlow(
        listOf(
            Task(1, "Estudiar para el examen", "Estudiar matemáticas", TaskStatus.TODO),
            Task(2, "Hacer la compra", "Comprar frutas y verduras", TaskStatus.IN_PROGRESS),
            Task(3, "Llamar al médico", "Pedir cita médica", TaskStatus.DONE)
        )
    )
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun addTask(title: String) {
        val newTask = Task(
            id = (_tasks.value.maxOfOrNull { it.id } ?: 0) + 1,
            title = title,
            description = "",
            status = TaskStatus.TODO
        )
        _tasks.value = _tasks.value + newTask
    }

    fun updateTask(updatedTask: Task) {
        _tasks.value = _tasks.value.map {
            if (it.id == updatedTask.id) updatedTask else it
        }
    }

    fun deleteTask(taskId: Long) {
        _tasks.value = _tasks.value.filter { it.id != taskId }
    }

    fun getTaskById(taskId: Long): Task? {
        return _tasks.value.find { it.id == taskId }
    }
}