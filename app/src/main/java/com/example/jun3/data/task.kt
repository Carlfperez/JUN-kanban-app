package com.example.jun3.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

// ===== ENTIDAD TAREA =====
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    @TypeConverters(TaskStatusConverter::class)
    val status: TaskStatus
)

enum class TaskStatus {
    TODO, IN_PROGRESS, DONE
}

// Convertidor para el enum TaskStatus
class TaskStatusConverter {
    @androidx.room.TypeConverter
    fun fromString(value: String): TaskStatus = TaskStatus.valueOf(value)
    @androidx.room.TypeConverter
    fun toString(status: TaskStatus): String = status.name
}

// ===== ENTIDAD SESIÓN DE ENFOQUE =====
@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long,
    val taskTitle: String,
    val startTime: Date,
    val endTime: Date? = null,
    val durationSeconds: Long = 0,
    val completed: Boolean = false
)

// Convertidor para Date
class DateConverter {
    @androidx.room.TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }
    @androidx.room.TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}