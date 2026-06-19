package com.example.jun3.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jun3.data.Task
import com.example.jun3.viewmodel.FocusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    task: Task,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    focusViewModel: FocusViewModel = viewModel()
) {
    val uiState by focusViewModel.uiState.collectAsState()

    // Iniciar la sesión de enfoque al entrar
    LaunchedEffect(task.id) {
        if (!uiState.isActive) {
            focusViewModel.startFocusSession(task)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "🎯 Enfoque",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        focusViewModel.stopFocusSession()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    if (uiState.isActive && !uiState.isPaused)
                        Color(0xFFE8F5E9)
                    else
                        Color(0xFFFFF3E0)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Información de la tarea
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Icono de estado
                    Icon(
                        imageVector = if (uiState.isActive && !uiState.isPaused)
                            Icons.Default.PlayArrow
                        else
                            Icons.Default.Pause,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = if (uiState.isActive && !uiState.isPaused)
                            Color(0xFF4CAF50)
                        else
                            Color(0xFFFF9800)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Tarea Actual",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Text(
                        text = uiState.currentTask?.title ?: task.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    if (!uiState.currentTask?.description.isNullOrEmpty()) {
                        Text(
                            text = uiState.currentTask?.description ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Temporizador
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            if (uiState.isActive && !uiState.isPaused)
                                Color(0xFF4CAF50).copy(alpha = 0.1f)
                            else
                                Color(0xFFFF9800).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(100.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = focusViewModel.formatTime(uiState.elapsedSeconds),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isActive && !uiState.isPaused)
                            Color(0xFF4CAF50)
                        else
                            Color(0xFFFF9800)
                    )
                }

                // Controles
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Botón Pausa/Reanudar
                        Button(
                            onClick = { focusViewModel.togglePause() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.isPaused)
                                    Color(0xFF4CAF50)
                                else
                                    Color(0xFFFF9800)
                            )
                        ) {
                            Icon(
                                imageVector = if (uiState.isPaused)
                                    Icons.Default.PlayArrow
                                else
                                    Icons.Default.Pause,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (uiState.isPaused) "Reanudar" else "Pausar")
                        }

                        // Botón Completar
                        Button(
                            onClick = {
                                focusViewModel.stopFocusSession()
                                onComplete()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Completar")
                        }
                    }

                    // Botón Detener
                    OutlinedButton(
                        onClick = {
                            focusViewModel.stopFocusSession()
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFD32F2F)
                        )
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Detener Sesión")
                    }
                }

                // Estadísticas rápidas
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = focusViewModel.formatTime(uiState.elapsedSeconds),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Tiempo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (uiState.isActive) "Activo" else "Inactivo",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (uiState.isActive)
                                    Color(0xFF4CAF50)
                                else
                                    Color(0xFFD32F2F)
                            )
                            Text(
                                text = "Estado",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}