package com.example.myprojecttreker.presentation.ui.main.taskeditor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.myprojecttreker.R
import com.example.myprojecttreker.data.audio.AppSounds
import com.example.myprojecttreker.data.audio.SoundPreviewer
import com.example.myprojecttreker.presentation.ui.taskicon.TaskIconSuggester
import com.example.myprojecttreker.data.subscription.SubscriptionManager
import com.example.myprojecttreker.data.settings.SettingsManager
import com.example.myprojecttreker.domain.ReminderOffset
import com.example.myprojecttreker.domain.RepeatType
import com.example.myprojecttreker.domain.Task
import com.example.myprojecttreker.presentation.ui.main.TaskIconView
import com.example.myprojecttreker.presentation.viewmodel.DayViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorScreen(
    task: Task?,
    viewModel: DayViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val subscriptionManager = remember { SubscriptionManager(context) }
    val settings by settingsManager.settings.collectAsState()
    val isPro by subscriptionManager.isPro.collectAsState()
    val isNew = task == null
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var state by remember(task?.id) {
        mutableStateOf(
            TaskEditorState(
                title = task?.title ?: "",
                description = task?.description ?: "",
                date = task?.date ?: LocalDate.now(),
                time = task?.time,
                repeatType = task?.repeatType ?: RepeatType.ONCE,
                weeklyDays = task?.repeatDays?.toSet() ?: emptySet(),
                courseDays = task?.courseDays ?: 1,
                dayOfMonth = task?.dayOfMonth ?: task?.date?.dayOfMonth,
                subtasks = task?.subtasks ?: emptyList(),
                extraTimes = task?.extraTimes ?: emptyList(),
                soundUri = if (task?.reminderOffset != null) task.soundUri ?: settings.defaultSoundUri else null,
                reminderOffset = task?.reminderOffset ?: task?.let { calculateOffset(it.date, it.time, it.remindAt) },
                iconId = task?.iconId ?: TaskIconSuggester.suggest(task?.title.orEmpty())
            )
        )
    }

    var showSoundPicker by remember { mutableStateOf(false) }
    var showIconPicker by remember { mutableStateOf(false) }
    var showProDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        viewModel.messages.collectLatest { key ->
            val message = when (key) {
                "free_limit_tasks" -> context.getString(R.string.free_limit_tasks)
                "free_limit_course" -> context.getString(R.string.free_limit_course)
                "sound_free_only" -> context.getString(R.string.sound_free_only)
                else -> key
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    fun selectRepeatType(type: RepeatType) {
        state = when (type) {
            RepeatType.MONTHLY -> state.copy(
                repeatType = type,
                dayOfMonth = state.dayOfMonth ?: state.date.dayOfMonth,
                extraTimes = emptyList()
            )
            RepeatType.COURSE -> state.copy(repeatType = type)
            else -> state.copy(
                repeatType = type,
                extraTimes = emptyList()
            )
        }
    }

    fun saveTask() {
        scope.launch {
            when {
                state.title.isBlank() -> snackbarHostState.showSnackbar(context.getString(R.string.title_required))
                state.reminderOffset != null && state.time == null -> snackbarHostState.showSnackbar(
                    context.getString(R.string.time_required_for_reminder)
                )
                else -> {
                    val remindAt = calculateRemindAt(state.date, state.time, state.reminderOffset)
                    if (remindAt != null &&
                        remindAt.isBefore(LocalDateTime.now()) &&
                        state.repeatType == RepeatType.ONCE
                    ) {
                        snackbarHostState.showSnackbar(context.getString(R.string.reminder_past))
                        return@launch
                    }

                    val taskToSave = Task(
                        id = task?.id ?: 0,
                        title = state.title.trim(),
                        description = state.description.trim(),
                        date = state.date,
                        time = state.time,
                        repeatType = state.repeatType,
                        repeatDays = if (state.repeatType == RepeatType.WEEKLY) state.weeklyDays.toList() else emptyList(),
                        dayOfMonth = if (state.repeatType == RepeatType.MONTHLY) {
                            state.dayOfMonth ?: state.date.dayOfMonth
                        } else null,
                        courseDays = if (state.repeatType == RepeatType.COURSE) state.courseDays.coerceIn(1, 99) else null,
                        subtasks = state.subtasks,
                        extraTimes = if (state.repeatType == RepeatType.COURSE) state.extraTimes else emptyList(),
                        soundUri = state.soundUri,
                        reminderOffset = state.reminderOffset,
                        remindAt = remindAt,
                        iconId = state.iconId
                    )
                    viewModel.saveTask(taskToSave, isNew) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isNew) stringResource(R.string.new_task)
                        else stringResource(R.string.edit_task)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            SaveButton(onClick = ::saveTask)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp)
        ) {
            item {
                TitleField(state) {
                    val suggestion = TaskIconSuggester.suggest(it)
                    state = state.copy(
                        title = it,
                        iconId = if (state.iconManuallySelected) state.iconId else suggestion
                    )
                }
            }
            item {
                DescriptionField(state) { state = state.copy(description = it) }
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showIconPicker = true }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TaskIconView(state.iconId)
                        Spacer(Modifier.padding(horizontal = 6.dp))
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.icon))
                            Text(
                                if (state.iconManuallySelected) "Выбрана пользователем"
                                else stringResource(R.string.suggested_icon),
                                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                            )
                        }
                        TextButton(onClick = { showIconPicker = true }) {
                            Text(stringResource(R.string.choose_icon))
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DatePickerField(
                        state = state,
                        modifier = Modifier.weight(1f),
                        onChange = { state = state.copy(date = it) },
                        onDayOfMonthChange = { state = state.copy(dayOfMonth = it) }
                    )
                    TimePickerField(
                        state = state,
                        modifier = Modifier.weight(1f),
                        onChange = { state = state.copy(time = it) }
                    )
                }
            }
            item {
                RepeatTypeSelector(
                    repeatType = state.repeatType,
                    onChange = ::selectRepeatType
                )
            }
            item {
                RepeatExtraFields(state) { state = it }
            }
            if (state.repeatType == RepeatType.COURSE) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                stringResource(R.string.extra_times),
                                style = androidx.compose.material3.MaterialTheme.typography.titleSmall
                            )
                            ExtraTimesBlock(
                                times = state.extraTimes,
                                canAddMore = subscriptionManager.canAddExtraTime(
                                    state.repeatType,
                                    state.extraTimes
                                ),
                                onLimitReached = { showProDialog = true },
                                onChange = { state = state.copy(extraTimes = it) }
                            )
                        }
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null)
                            Text(
                                stringResource(R.string.reminder),
                                Modifier.weight(1f)
                            )
                            Switch(
                                checked = state.reminderOffset != null,
                                onCheckedChange = { enabled ->
                                    if (enabled && Build.VERSION.SDK_INT >= 33 &&
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    state = state.copy(
                                        reminderOffset = if (enabled) {
                                            ReminderOffset(0, 0, settings.defaultReminderMinutes)
                                        } else null,
                                        soundUri = if (enabled) {
                                            state.soundUri ?: AppSounds.uri(
                                                context.packageName,
                                                AppSounds.all.first()
                                            )
                                        } else {
                                            state.soundUri
                                        }
                                    )
                                }
                            )
                        }
                        if (state.reminderOffset != null) {
                            ReminderBlock(state.reminderOffset) {
                                state = state.copy(reminderOffset = it)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showSoundPicker = true }
                                    .padding(vertical = 6.dp)
                            ) {
                                Text(
                                    stringResource(R.string.notification_sound),
                                    Modifier.weight(1f)
                                )
                                Text(
                                    soundName(context, state.soundUri),
                                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
            item {
                SubtasksEditor(
                    state = state,
                    onUpdate = { state = it }
                )
            }
        }
    }

    if (showIconPicker) {
        AlertDialog(
            onDismissRequest = { showIconPicker = false },
            title = { Text(stringResource(R.string.choose_icon)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskIconCatalogRow(state.iconId) { id ->
                        state = state.copy(iconId = id, iconManuallySelected = true)
                        showIconPicker = false
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIconPicker = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    if (showSoundPicker) {
        val previewer = remember { SoundPreviewer(context) }
        DisposableEffect(Unit) {
            onDispose { previewer.stop() }
        }
        AlertDialog(
            onDismissRequest = {
                previewer.stop()
                showSoundPicker = false
            },
            title = { Text(stringResource(R.string.notification_sound)) },
            text = {
                Column {
                    AppSounds.all.forEach { sound ->
                        val locked = !isPro && !sound.free
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !locked) {
                                    state = state.copy(
                                        soundUri = AppSounds.uri(context.packageName, sound)
                                    )
                                    showSoundPicker = false
                                }
                                .padding(vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.soundUri == AppSounds.uri(context.packageName, sound),
                                onClick = null
                            )
                            Text(
                                stringResource(sound.titleRes),
                                Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = { if (!locked) previewer.play(sound) }
                            ) {
                                Text("▶")
                            }
                            if (locked) {
                                Text(
                                    "PRO",
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    previewer.stop()
                    showSoundPicker = false
                }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    if (showProDialog) {
        AlertDialog(
            onDismissRequest = { showProDialog = false },
            title = { Text(stringResource(R.string.pro_required)) },
            text = { Text(stringResource(R.string.free_limit_course)) },
            dismissButton = {
                TextButton(onClick = { showProDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showProDialog = false
                    navController.navigate("pro")
                }) {
                    Text(stringResource(R.string.pro))
                }
            }
        )
    }
}

@Composable
private fun TaskIconCatalogRow(selected: String, onSelected: (String) -> Unit) {
    TaskIconPicker(selectedId = selected, onSelected = onSelected)
}

@Composable
private fun soundName(context: android.content.Context, uri: String?): String =
    context.getString(AppSounds.find(AppSounds.idFromUri(context.packageName, uri)).titleRes)

private fun calculateRemindAt(
    date: LocalDate,
    time: java.time.LocalTime?,
    offset: ReminderOffset?
): LocalDateTime? {
    if (offset == null || time == null) return null
    return LocalDateTime.of(date, time)
        .minusDays(offset.days.toLong())
        .minusHours(offset.hours.toLong())
        .minusMinutes(offset.minutes.toLong())
}

private fun calculateOffset(
    date: LocalDate,
    time: java.time.LocalTime?,
    remindAt: LocalDateTime?
): ReminderOffset? {
    if (time == null || remindAt == null) return null
    val base = LocalDateTime.of(date, time)
    val duration = java.time.Duration.between(remindAt, base)
    if (duration.isNegative) return null
    val days = duration.toDays()
    val hours = duration.minusDays(days).toHours()
    val minutes = duration.minusDays(days).minusHours(hours).toMinutes()
    return ReminderOffset(days.toInt(), hours.toInt(), minutes.toInt())
}
