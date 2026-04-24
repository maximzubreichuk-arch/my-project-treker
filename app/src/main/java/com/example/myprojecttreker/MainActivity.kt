package com.example.myprojecttreker


import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.myprojecttreker.data.local.database.AppDatabase
import com.example.myprojecttreker.data.mapper.toDomain
import com.example.myprojecttreker.data.reminder.scheduler.ReminderSchedulerImpl
import com.example.myprojecttreker.data.repository.RoomTaskRepository
import com.example.myprojecttreker.presentation.navigation.NavGraph
import com.example.myprojecttreker.domain.usecase.GetTasksForDay
import com.example.myprojecttreker.presentation.viewmodel.DayViewModel
import com.example.myprojecttreker.presentation.viewmodel.DayViewModelFactory
import kotlinx.coroutines.launch

/**
 * Главная Activity приложения.
 *
 * Отвечает за:
 * - инициализацию системы (БД, scheduler)
 * - запрос системных разрешений
 * - восстановление напоминаний
 * - запуск Compose UI
 */
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        /**
         * Разрешение на точные будильники (Android 12+)
         * необходимо для корректной работы напоминаний
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager =
                getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                )
                startActivity(intent)
            }
        }

        /**
         * Запрос отключения оптимизации батареи
         * (иначе система может убивать фоновые задачи и уведомления)
         */
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val asked = prefs.getBoolean("battery_asked", false)
        if (!asked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                startActivity(
                    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                        .setData(android.net.Uri.parse("package:$packageName"))
                )
                // Помечаем, что уже спрашивали пользователя
                prefs.edit().putBoolean("battery_asked", true).apply()
            }
        }

        // Инициализация базы данных
        val db = AppDatabase.getInstance(applicationContext)

        // Инициализация планировщика напоминаний
        val reminderScheduler = ReminderSchedulerImpl(applicationContext)

        /**
         * Восстановление всех напоминаний при запуске приложения
         * (например после перезагрузки устройства)
         */
        lifecycleScope.launch {
            val tasks = db.taskDao().getAllWithSubtasks()
            tasks.forEach {
                val task = it.toDomain()
                // Если у задачи есть напоминание, то планируем заново
                if (task.remindAt != null) {
                    reminderScheduler.schedule(task)
                }
            }
        }

        // Создание репозитория
        val repository = RoomTaskRepository(
            taskDao = db.taskDao(),
            subTaskDao = db.subTaskDao(),
            reminderScheduler = reminderScheduler
        )

        // UseCase для получения задач
        val getTasksForDay = GetTasksForDay(repository)

        // Фабрика для создания ViewModel
        val factory = DayViewModelFactory(
            getTasksForDay = getTasksForDay,
            repository = repository,
            dayResultDao = db.dayResultDao()
        )
        // Запуск Compose UI
        setContent {

            // Получаем ViewModel через factory
            val viewModel: DayViewModel =
                viewModel(factory = factory)

            // Навигационный контроллер
            val navController =
                rememberNavController()

            // Навигация приложения
            NavGraph(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
