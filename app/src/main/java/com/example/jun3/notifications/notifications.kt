package com.example.jun3.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.jun3.MainActivity
import com.example.jun3.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "jun_focus_channel"
        const val CHANNEL_NAME = "Jun Focus"
        const val NOTIFICATION_ID = 1001
    }

    // Crear canal de notificación (requerido en Android 8+)
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios de tareas activas"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Mostrar notificación de "retomar tarea"
    fun showFocusReminder(taskTitle: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("focus_reminder", true)
            putExtra("task_title", taskTitle)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("🎯 ¡Retoma tu tarea!")
            .setContentText("Tu tarea '$taskTitle' está esperando por ti")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                NotificationCompat.Action.Builder(
                    0,
                    "Retomar",
                    pendingIntent
                ).build()
            )
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    // Cancelar notificaciones
    fun cancelNotifications() {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.cancel(NOTIFICATION_ID)
    }
}