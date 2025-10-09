package com.example.jun3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jun3.navigation.Screen
import com.example.jun3.ui.theme.JUN3Theme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JUN3Theme {
                // Navigation setup
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onNavigateToTasks = { navController.navigate(Screen.TaskList.route) },
                            onNavigateToAbout = { navController.navigate(Screen.About.route) }
                        )
                    }

                    composable(Screen.TaskList.route) {
                        TaskListScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.AddTask.route) {
                        // Temporalmente
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text("Pantalla Agregar Tarea - En construcción")
                            Button(onClick = { navController.popBackStack() }) {
                                Text("Volver")
                            }
                        }
                    }

                    composable(Screen.About.route) {
                        AboutScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

// Data class para representar una tarea con estado
data class Task(
    val id: Int,
    val title: String,
    val status: TaskStatus
)

enum class TaskStatus {
    TODO, IN_PROGRESS, DONE
}

// Pantalla de Inicio
@Composable
fun HomeScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "JUN KANBAN",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
        )
        Text(
            text = "Bienvenido a JUN",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Tu organizador de tareas personal",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Botón para ir a Tareas
        Button(
            onClick = onNavigateToTasks,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Mis Tareas")
        }

        // Botón para ir a Acerca de
        OutlinedButton(
            onClick = onNavigateToAbout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Acerca de")
        }
    }
}

// TaskListScreen con sistema Kanban
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(onBack: () -> Unit) {
    var taskText by remember { mutableStateOf("") }
    var tasks by remember {
        mutableStateOf(
            listOf(
                Task(1, "Estudiar para el examen", TaskStatus.TODO),
                Task(2, "Hacer la compra", TaskStatus.IN_PROGRESS),
                Task(3, "Llamar al médico", TaskStatus.DONE)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Botón para volver atrás
        Button(
            onClick = onBack,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("← Volver al Inicio")
        }

        Text(
            text = "Tablero Kanban",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // TextField para nueva tarea
        TextField(
            value = taskText,
            onValueChange = { newText ->
                taskText = newText
            },
            label = { Text("Nueva tarea...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Botón para agregar tarea
        Button(
            onClick = {
                if (taskText.isNotBlank()) {
                    val newTask = Task(
                        id = (tasks.maxOfOrNull { it.id } ?: 0) + 1,
                        title = taskText,
                        status = TaskStatus.TODO
                    )
                    tasks = tasks + newTask
                    taskText = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Agregar Tarea a 'Por Hacer'")
        }

        // Las 3 columnas Kanban
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Columna 1: Por Hacer
            KanbanColumn(
                title = "Por Hacer",
                taskCount = tasks.count { it.status == TaskStatus.TODO },
                tasks = tasks.filter { it.status == TaskStatus.TODO },
                onTaskClick = { task ->
                    // Mover a En Progreso
                    tasks = tasks.map {
                        if (it.id == task.id) it.copy(status = TaskStatus.IN_PROGRESS)
                        else it
                    }
                },
                modifier = Modifier.weight(1f),
                columnColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Columna 2: En Progreso
            KanbanColumn(
                title = "En Progreso",
                taskCount = tasks.count { it.status == TaskStatus.IN_PROGRESS },
                tasks = tasks.filter { it.status == TaskStatus.IN_PROGRESS },
                onTaskClick = { task ->
                    // Mover a Completadas
                    tasks = tasks.map {
                        if (it.id == task.id) it.copy(status = TaskStatus.DONE)
                        else it
                    }
                },
                modifier = Modifier.weight(1f),
                columnColor = MaterialTheme.colorScheme.primaryContainer
            )

            // Columna 3: Completadas
            KanbanColumn(
                title = "Completadas",
                taskCount = tasks.count { it.status == TaskStatus.DONE },
                tasks = tasks.filter { it.status == TaskStatus.DONE },
                onTaskClick = { task ->
                    // Eliminar tarea
                    tasks = tasks.filter { it.id != task.id }
                },
                modifier = Modifier.weight(1f),
                columnColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        // Contador total de tareas
        Text(
            text = "Total de tareas: ${tasks.size}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

// Composable para cada columna Kanban
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanColumn(
    title: String,
    taskCount: Int,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier,
    columnColor: androidx.compose.ui.graphics.Color
) {
    Column(
        modifier = modifier
            .padding(4.dp)
    ) {
        // Header de la columna
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            colors = CardDefaults.cardColors(containerColor = columnColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "$taskCount tareas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        // Lista de tareas en la columna
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            items(tasks) { task ->
                KanbanTaskCard(
                    task = task,
                    onTaskClick = { onTaskClick(task) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Espacio vacío si no hay tareas
            if (tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay tareas",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

// Composable para cada tarjeta de tarea
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanTaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onTaskClick,
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji en lugar de icono (más simple y funciona)
            TaskIcon(taskTitle = task.title)

            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
// Función simplificada - usaremos emojis como texto
@Composable
fun TaskIcon(taskTitle: String) {
    val emoji = when {
        taskTitle.contains("estudiar", ignoreCase = true) -> "📚"
        taskTitle.contains("compr", ignoreCase = true) -> "🛒"
        taskTitle.contains("médic", ignoreCase = true) -> "🏥"
        taskTitle.contains("llamar", ignoreCase = true) -> "📞"
        taskTitle.contains("trabaj", ignoreCase = true) -> "💼"
        else -> "📝"
    }

    Text(
        text = emoji,
        modifier = Modifier
            .size(24.dp)
            .padding(end = 8.dp)
    )
}

// Pantalla Acerca de
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Botón para volver atrás
        Button(
            onClick = onBack,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Text("← Volver")
        }

        Text(
            text = "Acerca de JUN",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "JUN App",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Aplicación móvil para organización de tareas usando metodología Kanban.",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Desarrollada con Jetpack Compose y Kotlin.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Características",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("• Gestión visual de tareas")
                Text("• Interfaz intuitiva")
                Text("• Método Kanban")
                Text("• Desarrollada en Android Studio")
            }
        }
    }
}

// Previsualizaciones
@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    JUN3Theme {
        HomeScreen(
            onNavigateToTasks = { },
            onNavigateToAbout = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTaskListScreen() {
    JUN3Theme {
        TaskListScreen(onBack = { })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAboutScreen() {
    JUN3Theme {
        AboutScreen(onBack = { })
    }
}