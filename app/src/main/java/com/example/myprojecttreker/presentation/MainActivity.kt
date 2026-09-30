package com.example.myprojecttreker.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.myprojecttreker.data.ads.SessionManager
import com.example.myprojecttreker.presentation.localization.LocaleContextWrapper
import com.example.myprojecttreker.data.subscription.BillingManager
import com.example.myprojecttreker.data.subscription.SubscriptionManager
import com.example.myprojecttreker.data.local.AppDatabase
import com.example.myprojecttreker.data.reminder.ReminderSchedulerImpl
import com.example.myprojecttreker.data.repository.RoomTaskRepository
import com.example.myprojecttreker.data.settings.SettingsManager
import com.example.myprojecttreker.domain.usecase.GetTasksForDay
import com.example.myprojecttreker.presentation.navigation.NavGraph
import com.example.myprojecttreker.presentation.ui.main.MyProjectTrekerTheme
import com.example.myprojecttreker.presentation.viewmodel.DayViewModel
import com.example.myprojecttreker.presentation.viewmodel.DayViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var reminderScheduler: ReminderSchedulerImpl
    private lateinit var settingsManager: SettingsManager
    private lateinit var subscriptionManager: SubscriptionManager
    private lateinit var billingManager: BillingManager
    private lateinit var sessionManager: SessionManager
    private var showAdForSession: Boolean = false

    override fun attachBaseContext(newBase: Context) {
        val manager = SettingsManager(newBase)
        super.attachBaseContext(LocaleContextWrapper.wrap(newBase, manager.settings.value.languageTag))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        settingsManager = SettingsManager(applicationContext)
        subscriptionManager = SubscriptionManager(applicationContext)
        billingManager = BillingManager(applicationContext, subscriptionManager)
        sessionManager = SessionManager(applicationContext)
        val sessionNo = sessionManager.registerSession()
        showAdForSession = sessionNo % 3 == 0 && !subscriptionManager.isPro.value && !isDebugBuild()

        val db = AppDatabase.getInstance(applicationContext)
        reminderScheduler = ReminderSchedulerImpl(applicationContext)
        val repository = RoomTaskRepository(
            taskDao = db.taskDao(),
            subTaskDao = db.subTaskDao(),
            reminderScheduler = reminderScheduler
        )
        val getTasksForDay = GetTasksForDay(repository)
        val factory = DayViewModelFactory(
            getTasksForDay = getTasksForDay,
            repository = repository,
            dayResultDao = db.dayResultDao(),
            subscriptionManager = subscriptionManager
        )

        setContent {
            val settings = settingsManager.settings.collectAsStateWithLifecycle().value
            val viewModel: DayViewModel = viewModel(factory = factory)
            val navController = rememberNavController()
            val isPro = subscriptionManager.isPro.collectAsStateWithLifecycle().value
            MyProjectTrekerTheme(
                darkTheme = when (settings.themeMode) {
                    com.example.myprojecttreker.domain.settings.AppThemeMode.DARK -> true
                    com.example.myprojecttreker.domain.settings.AppThemeMode.LIGHT -> false
                    com.example.myprojecttreker.domain.settings.AppThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
                    else -> androidx.compose.foundation.isSystemInDarkTheme()
                },
                dynamicColor = false
            ) {
                NavGraph(
                    navController = navController,
                    viewModel = viewModel,
                    settingsManager = settingsManager,
                    subscriptionManager = subscriptionManager,
                    billingManager = billingManager,
                    showAd = showAdForSession && !isPro
                )
            }
        }
    }

    private fun isDebugBuild(): Boolean =
        (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            reminderScheduler.rescheduleAll()
        }
    }


}

