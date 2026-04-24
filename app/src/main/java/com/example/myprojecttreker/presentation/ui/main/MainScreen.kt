package com.example.myprojecttreker.presentation.ui.main

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myprojecttreker.data.local.entity.DayResultEntity
import com.example.myprojecttreker.domain.model.Task
import com.example.myprojecttreker.presentation.viewmodel.DayUiState
import com.example.myprojecttreker.presentation.viewmodel.DayViewModel
import java.time.LocalDate
import android.Manifest
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Today
import com.example.myprojecttreker.presentation.ui.calendar.MonthScreen
import com.example.myprojecttreker.presentation.ui.calendar.YearScreen
import com.example.myprojecttreker.presentation.viewmodel.DayIntent
import java.time.LocalTime

/**
 * Главный экран приложения.
 *
 * Отвечает за:
 * - переключение режимов (день / месяц / год)
 * - обработку свайпов
 * - отображение состояния UI (loading / content / error)
 * - навигацию
 */
@Composable
fun MainScreen(
    viewModel: DayViewModel,
    navController: NavController
) {
    // Подписка на состояние ViewModel
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Флаг, чтобы свайп обрабатывался только один раз за жест
    var swipeConsumed by remember { mutableStateOf(false) }

    // Текущий режим экрана (день / месяц / год)
    var screenMode by remember {
        mutableStateOf(ScreenMode.DAY)
    }

    Scaffold(

         // Кнопка добавления новой задачи
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("editor/0")
                }
            ) {
                Icon(Icons.Default.Add, null)
            }
        },


        //  Нижняя навигация (переключение режимов)
        bottomBar = {
            NavigationBar {
                // День
                NavigationBarItem(
                    selected = screenMode == ScreenMode.DAY,
                    onClick = { screenMode = ScreenMode.DAY },
                    icon = { Icon(Icons.Default.Today, null) },
                    label = { Text("День") }
                )
                // Месяц
                NavigationBarItem(
                    selected = screenMode == ScreenMode.MONTH,
                    onClick = { screenMode = ScreenMode.MONTH },
                    icon = { Icon(Icons.Default.DateRange, null) },
                    label = { Text("Месяц") }
                )
                // Год
                NavigationBarItem(
                    selected = screenMode == ScreenMode.YEAR,
                    onClick = { screenMode = ScreenMode.YEAR },
                    icon = { Icon(Icons.Default.CalendarMonth, null) },
                    label = { Text("Год") }
                )
            }
        }

    ) { padding ->

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)

                    /**
                     * Обработка свайпа:
                     * работает только в режиме DAY
                     */
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(

                            // Начало жеста - сбрасываем флаг
                            onDragStart = { swipeConsumed = false },
                            onHorizontalDrag = { _, dragAmount ->

                                // Разрешаем только один свайп за жест
                                if (!swipeConsumed && screenMode == ScreenMode.DAY) {

                                    // Свайп влево - следующий день
                                    if (dragAmount < -40) {
                                        viewModel.processIntent(DayIntent.NextDay)
                                        swipeConsumed = true

                                        // Свайп вправо - предыдущий день
                                    } else if (dragAmount > 40) {
                                        viewModel.processIntent(DayIntent.PreviousDay)
                                        swipeConsumed = true
                                    }
                                }
                            },

                            // Завершение жеста
                            onDragEnd = { swipeConsumed = false }
                        )
                    }
        ) {

            /**
             * Переключение экранов по режиму
             */
            when (screenMode) {

                ScreenMode.DAY -> {

                    /**
                     * Обработка состояний UI
                     */
                    when (state) {

                        // Загрузка
                        is DayUiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        // Нет задач
                        is DayUiState.Empty -> {
                            val empty = state as DayUiState.Empty
                            MainContent(
                                date = empty.date,
                                tasks = emptyList(),
                                dayResults = emptyList(),
                                viewModel = viewModel,
                                navController = navController
                            )
                        }

                        // Ошибка
                        is DayUiState.Error -> {
                            val error = state as DayUiState.Error
                            ErrorScreen(
                                message = error.message,
                                onRetry = {
                                    viewModel.processIntent(DayIntent.Retry)
                                }
                            )
                        }

                        // Контент
                        is DayUiState.Content -> {
                            val content = state as DayUiState.Content
                            MainContent(
                                date = content.date,
                                tasks = content.tasks,
                                dayResults = content.dayResults,
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                    }
                }

                /**
                 * Экран месяца
                 */
                ScreenMode.MONTH -> {
                    val content = state as? DayUiState.Content

                    MonthScreen(
                        currentDate = content?.date ?: LocalDate.now(),

                        viewModel = viewModel,

                        // При выборе даты переходим в DAY режим
                        onDateClick = { date ->
                            viewModel.processIntent(DayIntent.SelectDate(date))
                            screenMode = ScreenMode.DAY
                        }
                    )

                }

                /**
                 * Экран года
                 */
                ScreenMode.YEAR -> {

                    val content = state as? DayUiState.Content

                    YearScreen(
                        currentDate = content?.date ?: LocalDate.now(),
                        dayResults = content?.dayResults ?: emptyList(),
                        viewModel = viewModel,

                        // При выборе даты переходим в DAY режим
                        onDateClick = { date ->
                            viewModel.processIntent(DayIntent.SelectDate(date))
                            screenMode = ScreenMode.DAY
                        }
                    )
                }
            }
        }
    }

    /**
     * Запрос разрешения на уведомления (Android 13+)
     */
    if (Build.VERSION.SDK_INT >= 33) {
        val permission = Manifest.permission.POST_NOTIFICATIONS

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = {}
        )

        LaunchedEffect(Unit) {
            launcher.launch(permission)
        }
    }
}

/**
 * Основной контент дня:
 * - разворачивает задачи во временные инстансы
 * - сортирует их
 * - отображает список
 */
@Composable
private fun MainContent(
    date: LocalDate,
    tasks: List<Task>,
    dayResults: List<DayResultEntity>,
    viewModel: DayViewModel,
    navController: NavController
) {

    /**
     * Превращаем задачи в инстансы:
     * одна задача → несколько (по времени)
     */
    val taskInstances = tasks.flatMap { task ->

        val times = buildList {
            task.time?.let { add(it) }
            addAll(task.extraTimes)
        }

        // Если нет времени — один инстанс без времени
        if (times.isEmpty()) {
            listOf(TaskInstance(task, null as LocalTime?))
        } else {

            // Иначе — инстанс на каждое время
            times.map { time ->
                TaskInstance(task, time)
            }
        }
    }

    /**
     * Сортировка:
     * - сначала задачи без времени
     * - затем по времени
     */
    val sortedInstances = taskInstances.sortedWith(
        compareBy<TaskInstance> { it.time == null }
            .thenBy { it.time }
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {

        // Заголовок даты
        item {
            DateHeader(date)
        }

        // Список задач
        items(
            items = sortedInstances,
            key = { "${it.task.id}_${it.time}" }
        ) { instance ->
            TaskItem(
                task = instance.task,
                time = instance.time,
                selectedDate = date,
                dayResults = dayResults,
                expanded = instance.task.isExpanded,
                onExpandedChange = {
                    viewModel.processIntent(
                        DayIntent.ToggleExpand(instance.task)
                    )
                },
                onDelete = {
                    viewModel.processIntent(
                        DayIntent.DeleteTask(it)
                    )
                },
                onEdit = {
                    navController.navigate("editor/${it.id}")
                },

                // Обработка отметки выполнения
                onToggleDayResult = { taskId, selectedDate, timeKey, isDone, subtasks ->
                    viewModel.processIntent(
                        DayIntent.ToggleDayResult(
                            taskId = taskId,
                            date = selectedDate,
                            time = timeKey,
                            isDone = isDone,
                            subtasks = subtasks
                        )
                    )
                }
            )
        }


    }
}

/**
 * Заголовок с датой
 */
@Composable
private fun DateHeader(
    date: LocalDate
) {
    Text(
        text = date.toString(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        style = MaterialTheme.typography.titleLarge
    )
}