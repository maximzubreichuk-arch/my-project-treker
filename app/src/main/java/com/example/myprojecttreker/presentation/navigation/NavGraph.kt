package com.example.myprojecttreker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myprojecttreker.presentation.ui.main.MainScreen
import com.example.myprojecttreker.presentation.ui.taskeditor.TaskEditorScreen
import com.example.myprojecttreker.presentation.viewmodel.DayViewModel


/**
 * Главный навигационный граф приложения.
 *
 * Отвечает за:
 * - описание всех экранов (routes)
 * - переходы между ними
 * - передачу аргументов (например taskId)
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: DayViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "day"
    ) {
        // Главный экран (день / список задач)
        composable("day") {
            MainScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        // Экран создания/редактирования задачи
        composable(
            route = "editor/{taskId}"
        ) { backStackEntry ->

            // Получаем taskId из аргументов навигации
            val taskId =
                backStackEntry.arguments
                    ?.getString("taskId")
                    ?.toLongOrNull()
                    ?: 0L

            // Получаем задачу из ViewModel (если id != 0)
            val task = viewModel.getTaskById(taskId)

            // Открываем экран редактора
            TaskEditorScreen(
                task = task,
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}