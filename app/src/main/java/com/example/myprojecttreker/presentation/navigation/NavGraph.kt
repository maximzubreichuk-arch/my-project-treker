package com.example.myprojecttreker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myprojecttreker.data.subscription.BillingManager
import com.example.myprojecttreker.data.subscription.SubscriptionManager
import com.example.myprojecttreker.data.settings.SettingsManager
import com.example.myprojecttreker.presentation.ui.main.MainScreen
import com.example.myprojecttreker.presentation.ui.details.TaskDetailsScreen
import com.example.myprojecttreker.presentation.ui.main.taskeditor.TaskEditorScreen
import com.example.myprojecttreker.presentation.ui.pro.ProScreen
import com.example.myprojecttreker.presentation.ui.settings.SettingsScreen
import com.example.myprojecttreker.presentation.viewmodel.DayViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: DayViewModel,
    settingsManager: SettingsManager,
    subscriptionManager: SubscriptionManager,
    billingManager: BillingManager,
    showAd: Boolean
) {
    NavHost(navController = navController, startDestination = "day") {
        composable("day") {
            MainScreen(viewModel, navController, showAd)
        }
        composable("editor/{taskId}") { entry ->
            val taskId = entry.arguments?.getString("taskId")?.toLongOrNull() ?: 0L
            TaskEditorScreen(viewModel.getTaskById(taskId), viewModel, navController)
        }
        composable("details/{taskId}") { entry ->
            val taskId = entry.arguments?.getString("taskId")?.toLongOrNull() ?: 0L
            val task = viewModel.getTaskById(taskId)
            if (task != null) TaskDetailsScreen(task, navController)
        }
        composable("settings") {
            SettingsScreen(
                settingsManager = settingsManager,
                subscriptionManager = subscriptionManager,
                billingManager = billingManager,
                onBack = { navController.popBackStack() },
                onOpenPro = { navController.navigate("pro") }
            )
        }
        composable("pro") {
            ProScreen(subscriptionManager, billingManager) { navController.popBackStack() }
        }
    }
}
