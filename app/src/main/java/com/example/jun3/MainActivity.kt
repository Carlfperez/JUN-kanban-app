package com.example.jun3

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jun3.data.Task
import com.example.jun3.data.TaskStatus
import com.example.jun3.navigation.Screen
import com.example.jun3.screens.AboutScreen
import com.example.jun3.screens.FocusScreen
import com.example.jun3.screens.SettingsScreen
import com.example.jun3.screens.VideoScreen
import com.example.jun3.ui.theme.JUN3Theme
import com.example.jun3.utils.PreferenceHelper
import com.example.jun3.viewmodel.FocusViewModel
import com.example.jun3.viewmodel.TaskListViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    // ✅ ViewModel con Application (Factory)
    private val taskListViewModel: TaskListViewModel by viewModels {
        TaskListViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        if (intent.getBooleanExtra("focus_reminder", false)) {
            // La app se abrió desde la notificación
        }
        setContent {
            val context = LocalContext.current
            val prefs = remember { PreferenceHelper(context) }
            var todayFocusTime by remember { mutableStateOf("0s") }

            // Actualizar el tiempo cada 5 segundos
            LaunchedEffect(Unit) {
                while (true) {
                    val seconds = prefs.getTodayFocusTime()
                    todayFocusTime = prefs.formatTime(seconds)
                    delay(5000)
                }
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                onNavigateToTasks = { navController.navigate(Screen.TaskList.route) },
                                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                                todayFocusTime = todayFocusTime
                            )
                        }

                        composable(Screen.TaskList.route) {
                            TaskListScreen(
                                onBack = { navController.popBackStack() },
                                navController = navController,
                                viewModel = taskListViewModel
                            )
                        }

                        composable(Screen.AddTask.route) {
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
                                onBack = { navController.popBackStack() },
                                navController = navController
                            )
                        }

                        composable(Screen.VideoPlayer.route) {
                            VideoScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                navController = navController
                            )
                        }

                        // Pantalla de Enfoque
                        composable(
                            route = "focus/{taskId}",
                            arguments = listOf(navArgument("taskId") { defaultValue = 0L })
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
                            // Obtener la tarea desde Room (suspend)
                            var task by remember { mutableStateOf<Task?>(null) }
                            var isLoading by remember { mutableStateOf(true) }

                            LaunchedEffect(taskId) {
                                isLoading = true
                                task = taskListViewModel.getTaskById(taskId)
                                isLoading = false
                            }

                            if (isLoading || task == null) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                val focusViewModel: FocusViewModel = viewModel(
                                    factory = FocusViewModel.provideFactory(application)
                                )

                                FocusScreen(
                                    task = task!!,
                                    onBack = { navController.popBackStack() },
                                    onComplete = {
                                        taskListViewModel.updateTask(
                                            task!!.copy(status = TaskStatus.DONE)
                                        )
                                        navController.popBackStack()
                                    },
                                    focusViewModel = focusViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== FACTORY PARA TASKLISTVIEWMODEL ====================

class TaskListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// ==================== PANTALLA DE INICIO ====================

@Composable
fun HomeScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToSettings: () -> Unit,
    todayFocusTime: String
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

        // Card: Tiempo enfocado hoy
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⏱️ Tiempo enfocado hoy",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = todayFocusTime,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Button(
            onClick = onNavigateToTasks,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("📋 Mis Tareas")
        }

        OutlinedButton(
            onClick = onNavigateToSettings,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("⚙️ Ajustes")
        }

        OutlinedButton(
            onClick = onNavigateToAbout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("ℹ️ Acerca de")
        }
    }
}

// ==================== TABLERO KANBAN ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onBack: () -> Unit,
    navController: NavController,
    viewModel: TaskListViewModel
) {
    val tasks by viewModel.tasks.collectAsState()
    var taskText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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

        TextField(
            value = taskText,
            onValueChange = { newText -> taskText = newText },
            label = { Text("Nueva tarea...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Button(
            onClick = {
                if (taskText.isNotBlank()) {
                    viewModel.addTask(taskText)
                    taskText = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Agregar Tarea a 'Por Hacer'")
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            KanbanColumn(
                title = "Por Hacer",
                taskCount = tasks.count { it.status == TaskStatus.TODO },
                tasks = tasks.filter { it.status == TaskStatus.TODO },
                onTaskClick = { task ->
                    viewModel.updateTask(task.copy(status = TaskStatus.IN_PROGRESS))
                },
                onFocusClick = { /* No aplica */ },
                modifier = Modifier.weight(1f),
                columnColor = MaterialTheme.colorScheme.surfaceVariant
            )

            KanbanColumn(
                title = "En Progreso",
                taskCount = tasks.count { it.status == TaskStatus.IN_PROGRESS },
                tasks = tasks.filter { it.status == TaskStatus.IN_PROGRESS },
                onTaskClick = { task ->
                    viewModel.updateTask(task.copy(status = TaskStatus.DONE))
                },
                onFocusClick = { task ->
                    navController.navigate("focus/${task.id}")
                },
                modifier = Modifier.weight(1f),
                columnColor = MaterialTheme.colorScheme.primaryContainer
            )

            KanbanColumn(
                title = "Completadas",
                taskCount = tasks.count { it.status == TaskStatus.DONE },
                tasks = tasks.filter { it.status == TaskStatus.DONE },
                onTaskClick = { task ->
                    viewModel.deleteTask(task.id)
                },
                onFocusClick = { /* No aplica */ },
                modifier = Modifier.weight(1f),
                columnColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        Text(
            text = "Total de tareas: ${tasks.size}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

// ==================== COLUMNA KANBAN ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanColumn(
    title: String,
    taskCount: Int,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onFocusClick: (Task) -> Unit,
    modifier: Modifier = Modifier,
    columnColor: androidx.compose.ui.graphics.Color
) {
    Column(
        modifier = modifier
            .padding(4.dp)
    ) {
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

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            items(tasks) { task ->
                KanbanTaskCard(
                    task = task,
                    onTaskClick = { onTaskClick(task) },
                    onFocusClick = { onFocusClick(task) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

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

// ==================== TARJETA DE TAREA ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanTaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    onFocusClick: () -> Unit,
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
            TaskIcon(taskTitle = task.title)

            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            if (task.status == TaskStatus.IN_PROGRESS) {
                IconButton(
                    onClick = onFocusClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Iniciar enfoque",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// ==================== ICONO DE TAREA ====================

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

// ==================== ACERCA DE ====================

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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

// ==================== PREVIEWS ====================

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    JUN3Theme {
        HomeScreen(
            onNavigateToTasks = { },
            onNavigateToAbout = { },
            onNavigateToSettings = { },
            todayFocusTime = "1h 23m"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTaskListScreen() {
    JUN3Theme {
        // Para preview, usamos un ViewModel dummy (no persistente)
        val dummyViewModel = TaskListViewModel(androidx.compose.ui.platform.LocalContext.current.applicationContext as Application)
        TaskListScreen(
            onBack = { },
            navController = rememberNavController(),
            viewModel = dummyViewModel
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAboutScreen() {
    JUN3Theme {
        AboutScreen(onBack = { })
    }
}