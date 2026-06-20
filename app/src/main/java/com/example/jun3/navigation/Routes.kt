package com.example.jun3.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TaskList : Screen("task_list")
    object AddTask : Screen("add_task")
    object About : Screen("about")
    object VideoPlayer : Screen("video_player")  // RUTA DE VIDEO

    object Focus : Screen("focus")

    object Settings : Screen("settings")
}
