package com.example.jun3.data

// Data class unificada para Task
data class Task(
    val id: Long,
    val title: String,
    val description: String = "",
    val status: TaskStatus
)

enum class TaskStatus {
    TODO, IN_PROGRESS, DONE
}

enum class Priority {
    LOW, NORMAL, HIGH
}
// timestamp: 2026-05-20 18:35:35
