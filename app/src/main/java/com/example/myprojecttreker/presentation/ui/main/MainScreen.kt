package com.example.myprojecttreker.presentation.ui.main

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myprojecttreker.R
import com.example.myprojecttreker.data.ads.AdBanner
import com.example.myprojecttreker.data.local.DayResultEntity
import com.example.myprojecttreker.domain.Task
import com.example.myprojecttreker.presentation.ui.main.year.YearScreen
import com.example.myprojecttreker.presentation.viewmodel.DayUiState
import com.example.myprojecttreker.presentation.viewmodel.DayViewModel
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun MainScreen(
    viewModel: DayViewModel,
    navController: NavController,
    showAd: Boolean
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var swipeConsumed by remember { mutableStateOf(false) }
    var screenMode by remember { mutableStateOf(ScreenMode.DAY) }

    Scaffold(
        floatingActionButton = {
            if (screenMode == ScreenMode.DAY) {
                FloatingActionButton(onClick = { navController.navigate("editor/0") }) { Icon(Icons.Default.Add, null) }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(screenMode == ScreenMode.DAY, { screenMode = ScreenMode.DAY }, icon = { Icon(Icons.Default.Today, null) }, label = { Text(stringResource(R.string.today)) })
                NavigationBarItem(screenMode == ScreenMode.MONTH, { screenMode = ScreenMode.MONTH }, icon = { Icon(Icons.Default.DateRange, null) }, label = { Text(stringResource(R.string.month)) })
                NavigationBarItem(screenMode == ScreenMode.YEAR, { screenMode = ScreenMode.YEAR }, icon = { Icon(Icons.Default.CalendarMonth, null) }, label = { Text(stringResource(R.string.year)) })
                NavigationBarItem(false, { navController.navigate("settings") }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text(stringResource(R.string.settings)) })
            }
        }
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding).pointerInput(screenMode) {
                detectHorizontalDragGestures(
                    onDragStart = { swipeConsumed = false },
                    onHorizontalDrag = { _, dragAmount ->
                        if (!swipeConsumed && screenMode == ScreenMode.DAY) {
                            when {
                                dragAmount < -40 -> { viewModel.processIntent(DayIntent.NextDay); swipeConsumed = true }
                                dragAmount > 40 -> { viewModel.processIntent(DayIntent.PreviousDay); swipeConsumed = true }
                            }
                        }
                    },
                    onDragEnd = { swipeConsumed = false }
                )
            }
        ) {
            when (screenMode) {
                ScreenMode.DAY -> when (val current = state) {
                    is DayUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    is DayUiState.Empty -> MainContent(current.date, emptyList(), emptyList(), viewModel, navController, showAd)
                    is DayUiState.Error -> ErrorScreen(current.message) { viewModel.processIntent(DayIntent.Retry) }
                    is DayUiState.Content -> MainContent(current.date, current.tasks, current.dayResults, viewModel, navController, showAd)
                }
                ScreenMode.MONTH -> {
                    val content = state as? DayUiState.Content
                    MonthScreen(content?.date ?: LocalDate.now(), viewModel) { date -> viewModel.processIntent(DayIntent.SelectDate(date)); screenMode = ScreenMode.DAY }
                }
                ScreenMode.YEAR -> {
                    val content = state as? DayUiState.Content
                    YearScreen(content?.date ?: LocalDate.now(), content?.dayResults ?: emptyList(), viewModel) { date -> viewModel.processIntent(DayIntent.SelectDate(date)); screenMode = ScreenMode.DAY }
                }
            }
        }
    }
}


@Composable
private fun MainContent(
    date: LocalDate,
    tasks: List<Task>,
    dayResults: List<DayResultEntity>,
    viewModel: DayViewModel,
    navController: NavController,
    showAd: Boolean
) {
    val taskInstances = tasks.flatMap { task ->
        val times = buildList { task.time?.let(::add); addAll(task.extraTimes) }
        if (times.isEmpty()) listOf(TaskInstance(task, null)) else times.map { TaskInstance(task, it) }
    }
    val sortedInstances = taskInstances.sortedWith(compareBy<TaskInstance> { it.time == null }.thenBy { it.time })
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item { DateHeader(date) }
        items(sortedInstances, key = { "${it.task.id}_${it.time}" }) { instance ->
            TaskItem(
                task = instance.task,
                time = instance.time,
                selectedDate = date,
                dayResults = dayResults,
                expanded = instance.task.isExpanded,
                onExpandedChange = { viewModel.processIntent(DayIntent.ToggleExpand(instance.task)) },
                onDelete = { viewModel.processIntent(DayIntent.DeleteTask(it)) },
                onEdit = { navController.navigate("editor/${it.id}") },
                onOpen = { navController.navigate("details/${it.id}") },
                onToggleDayResult = { taskId, selectedDate, timeKey, isDone, subtasks ->
                    viewModel.processIntent(DayIntent.ToggleDayResult(taskId, selectedDate, timeKey, isDone, subtasks))
                }
            )
        }
        if (showAd) {
            item {
                AdBanner(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp))
            }
        }
    }
}

@Composable
private fun DateHeader(date: LocalDate) {
    Text(date.toString(), Modifier.fillMaxWidth().padding(16.dp), style = MaterialTheme.typography.titleLarge)
}
