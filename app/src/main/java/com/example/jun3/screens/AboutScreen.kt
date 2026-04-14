package com.example.jun3.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jun3.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit,
    navController: NavController // ← AGREGAR este parámetro
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Acerca de") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            Text(
                text = "JUN KANBAN",
                style = MaterialTheme.typography.headlineMedium
            )

            // Descripción
            Text(
                text = "Tu aplicación de gestión de tareas personal",
                style = MaterialTheme.typography.bodyLarge
            )

            // Información de versión
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Información de la App",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Versión: 1.0.0")
                    Text("Desarrollado con Jetpack Compose")
                    Text("Navegación con Compose Navigation")
                }
            }

            // BOTÓN DE VIDEO TUTORIAL - NUEVO
            Button(
                onClick = {
                    navController.navigate(Screen.VideoPlayer.route)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors()
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Video Tutorial"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Video Tutorial")
            }

            // Características
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Características",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Gestión de tareas")
                    Text("• Interfaz moderna")
                    Text("• Navegación fluida")
                    Text("• Diseño responsivo")
                }
            }
        }
    }
}