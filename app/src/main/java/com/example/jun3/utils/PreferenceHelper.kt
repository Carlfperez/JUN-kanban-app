package com.example.jun3.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar

class PreferenceHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("jun_prefs", Context.MODE_PRIVATE)

    // Tiempo enfocado
    fun saveFocusTime(seconds: Long) {
        val today = getTodayDate()
        val currentTotal = prefs.getLong("focus_time_$today", 0)
        prefs.edit().putLong("focus_time_$today", currentTotal + seconds).apply()
    }

    fun getTodayFocusTime(): Long {
        val today = getTodayDate()
        return prefs.getLong("focus_time_$today", 0)
    }

    fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${remainingSeconds}s"
            else -> "${remainingSeconds}s"
        }
    }

    //Configuración del tiempo de espera de nudges
    fun saveNudgeDelay(minutes: Int) {
        prefs.edit().putInt("nudge_delay", minutes).apply()
    }

    fun getNudgeDelay(): Int {
        return prefs.getInt("nudge_delay", 2) // 2 minutos por defecto
    }

    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
    }
}