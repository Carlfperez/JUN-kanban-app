package com.example.jun3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jun3.data.Task
import com.example.jun3.data.TaskDatabase
import com.example.jun3.data.TaskRepository
import com.example.jun3.data.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository(TaskDatabase.getInstance(application).taskDao())

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            repository.getAllTasks().collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    fun addTask(title: String) {
        viewModelScope.launch {
            val newTask = Task(
                title = title,
                description = "",
                status = TaskStatus.TODO
            )
            repository.insertTask(newTask)
        }
    }

    fun updateTask(updatedTask: Task) {
        viewModelScope.launch {
            repository.updateTask(updatedTask)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTaskById(taskId)
        }
    }

    suspend fun getTaskById(taskId: Long): Task? {
        return repository.getTaskById(taskId)
    }
}