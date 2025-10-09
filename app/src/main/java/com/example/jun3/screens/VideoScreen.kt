package com.example.jun3.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var videoView by remember { mutableStateOf<VideoView?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Video Tutorial") },
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
                .padding(16.dp)
        ) {
            // Título del video
            Text(
                text = "Tutorial JUN KANBAN",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // REPRODUCTOR MEJORADO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                AndroidView(
                    factory = { androidContext -> // Cambié 'context' por 'androidContext'
                        VideoView(androidContext).apply {
                            videoView = this

                            // Configurar controles de video MEJORADO
                            val mediaController = MediaController(androidContext)
                            mediaController.setAnchorView(this)
                            setMediaController(mediaController)
                            mediaController.setMediaPlayer(this)

                            // CARGAR TU VIDEO
                            val videoUri = Uri.parse("android.resource://${androidContext.packageName}/raw/jun_funcionamiento")

                            setVideoURI(videoUri)

                            // Listener para cuando el video está listo
                            setOnPreparedListener { mp ->
                                mp.start()
                                mediaController.show(0) // Mostrar controles
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // BOTÓN REINICIAR FUNCIONAL
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        videoView?.apply {
                            seekTo(0) // Ir al inicio
                            start()   // Reproducir
                        }
                    }
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Reiniciar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reiniciar Video")
                }
            }

            // Descripción
            Text(
                text = "Video demostrativo de todas las funciones de JUN KANBAN",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Instrucciones
            Text(
                text = "💡 Toca el video para ver controles de reproducción",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}