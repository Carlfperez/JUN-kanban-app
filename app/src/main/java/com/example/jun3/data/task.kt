package com.example.jun3.data

// los imports de Room:
// import androidx.room.Entity
// import androidx.room.PrimaryKey
// import androidx.room.TypeConverter
// import androidx.room.TypeConverters

// Data class simple sin Room
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: String? = null,
    val priority: Priority = Priority.NORMAL,
    val status: TaskStatus
)

enum class Priority { LOW, NORMAL, HIGH }
enum class TaskStatus { TODO, IN_PROGRESS, DONE }

// no se usa sin Room:
/*
class Converters {
    @TypeConverter fun fromPriority(priority: Priority): String = priority.name
    @TypeConverter fun toPriority(priority: String): Priority = Priority.valueOf(priority)
    @TypeConverter fun fromTaskStatus(status: TaskStatus): String = status.name
    @TypeConverter fun toTaskStatus(status: String): TaskStatus = TaskStatus.valueOf(status)
}
*/