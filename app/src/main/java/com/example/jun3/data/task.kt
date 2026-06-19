package com.example.jun3.data
import java.util.Date
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
// NUEVA CLASE PARA SESIONES DE ENFOQUE
data class FocusSession(
    val id: Long = 0,
    val taskId: Long,
    val taskTitle: String,
    val startTime: Date,
    val endTime: Date? = null,
    val durationSeconds: Long = 0,
    val completed: Boolean = false
)

enum class Priority {
    LOW, NORMAL, HIGH
}

