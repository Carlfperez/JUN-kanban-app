package com.example.jun3.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TaskList : Screen("task_list")
    object AddTask : Screen("add_task")
    object About : Screen("about")
    object VideoPlayer : Screen("video_player")  // ← NUEVA RUTA
}
// timestamp: 2026-05-20 18:35:35
