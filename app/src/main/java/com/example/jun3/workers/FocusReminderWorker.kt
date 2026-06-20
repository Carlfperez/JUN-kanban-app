package com.example.jun3.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.jun3.notifications.NotificationHelper

class FocusReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val taskTitle = inputData.getString("task_title") ?: return Result.failure()

            // Mostrar la notificación
            val notificationHelper = NotificationHelper(applicationContext)
            notificationHelper.createNotificationChannel()
            notificationHelper.showFocusReminder(taskTitle)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}