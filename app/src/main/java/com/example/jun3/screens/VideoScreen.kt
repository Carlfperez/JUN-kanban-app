package com.example.jun3.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var videoView by remember { mutableStateOf<VideoView?>(null) }
    var isPlaying by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("🎬 Tutorial JUN KANBAN") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // REPRODUCTOR MEJORADO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    AndroidView(
                        factory = { androidContext ->
                            VideoView(androidContext).apply {
                                videoView = this

                                // Configurar controles MEJORADOS
                                val mediaController = MediaController(androidContext)
                                mediaController.setAnchorView(this)
                                setMediaController(mediaController)
                                mediaController.setMediaPlayer(this)

                                // CARGAR VIDEO
                                val videoUri = Uri.parse("android.resource://${androidContext.packageName}/raw/jun_funcionamiento")
                                setVideoURI(videoUri)

                                // Listeners para estado del video
                                setOnPreparedListener { mp ->
                                    mp.start()
                                    mediaController.show(0)
                                    isPlaying = true
                                }

                                setOnTouchListener { _, _ ->
                                    showControls = !showControls
                                    true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // CONTROLES PERSONALIZADOS - SOLO CON ICONOS BÁSICOS
                    if (showControls) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Botón Retroceso 10s - CON TEXTO
                                Button(
                                    onClick = {
                                        videoView?.apply {
                                            seekTo(currentPosition - 10000)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                                ) {
                                    Text("« 10s", color = Color.Black)
                                }

                                // Botón Play/Pause - CON TEXTO
                                Button(
                                    onClick = {
                                        videoView?.apply {
                                            if (isPlaying) {
                                                pause()
                                                isPlaying = false
                                            } else {
                                                start()
                                                isPlaying = true
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Text(if (isPlaying) "⏸️" else "▶️", color = Color.Black)
                                }

                                // Botón Avance 10s - CON TEXTO
                                Button(
                                    onClick = {
                                        videoView?.apply {
                                            seekTo(currentPosition + 10000)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                                ) {
                                    Text("10s »", color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }

            // PANEL DE CONTROL MEJORADO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Fila de controles principales
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Reiniciar
                    Button(
                        onClick = {
                            videoView?.apply {
                                seekTo(0)
                                start()
                                isPlaying = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("🔄 Reiniciar")
                    }

                    // Mostrar/ocultar controles
                    Button(
                        onClick = {
                            showControls = !showControls
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("🎮 Controles")
                    }
                }

                // Información del video
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📱 Demo Completa de JUN KANBAN",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Gestión de tareas\n• Navegación entre pantallas\n• Interfaz moderna",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
// timestamp: 2026-05-20 18:35:35
