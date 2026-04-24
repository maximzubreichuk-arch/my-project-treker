package com.example.myprojecttreker.presentation.ui.taskeditor



import android.widget.Toast
import com.example.myprojecttreker.domain.model.Task
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myprojecttreker.domain.model.ReminderOffset
import com.example.myprojecttreker.domain.model.RepeatType
import com.example.myprojecttreker.presentation.viewmodel.DayViewModel
import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import com.example.myprojecttreker.data.settings.SettingsManager
import com.example.myprojecttreker.presentation.viewmodel.DayIntent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Duration

/**
 * Экран создания/редактирования задачи.
 *
 * Позволяет:
 * - задать параметры задачи
 * - настроить повторения
 * - добавить подзадачи
 * - настроить напоминание и звук
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorScreen(
    task: Task?,
    viewModel: DayViewModel,
    navController: NavController
) {
    // Определяем: создаём новую задачу или редактируем существующую
    val isNew = task == null

    val context = LocalContext.current

    // Менеджер настроек (используется для хранения звука по умолчанию)
    val settings = SettingsManager(context)

    // Инициализация состояния формы:
    // - при редактировании - заполняем из task
    // - при создании - используем значения по умолчанию
    // reminderOffset вычисляется из remindAt (обратное преобразование)
    var state by remember {
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
                soundUri = task?.soundUri,
                reminderOffset =
                    task?.let {
                        // вычисляем offset из сохранённого remindAt
                        calculateOffset(
                            task.date,
                            task.time,
                            task.remindAt
                        )
                    }
            )
        )
    }
    // Launcher для выбора звука уведомления через системный экран
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data
                ?.getParcelableExtra<Uri>(
                    RingtoneManager.EXTRA_RINGTONE_PICKED_URI
                )
            // Сохраняем выбранный звук в состоянии
            state = state.copy(
                soundUri = uri?.toString()
            )
            // Если звук по умолчанию ещё не задан — сохраняем
            if (settings.getDefaultSound() == null) {
                settings.saveDefaultSound(uri?.toString())
            }
        }
    }
    // Состояние для отображения ошибок пользователю
    var showError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isNew) "Новая задача" else "Редактирование")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // Возврат назад без сохранения
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SaveButton {

                    // нельзя включить напоминание без времени задачи
                    if (state.reminderOffset != null && state.time == null) {
                        showError = "Укажите время или уберите напоминание"
                        return@SaveButton
                    }

                    // временная модель (используется для логики, НЕ сохраняется)
                    val tempTask = Task(
                        title = state.title,
                        date = state.date,
                        time = state.time,
                        repeatType = state.repeatType,
                        repeatDays = state.weeklyDays.toList()
                    )

                    // Рассчитываем время напоминания
                    val remindAt = calculateRemindAt(
                        date = state.date,
                        time = state.time,
                        offset = state.reminderOffset
                    )
                    // Проверка: напоминание не должно быть в прошлом
                    if (remindAt != null) {
                        val now = LocalDateTime.now()
                        if (remindAt.isBefore(now)) {
                            showError = "Напоминание уже в прошлом"
                            return@SaveButton
                        }
                    }

                    // Формируем финальную задачу для сохранения
                    val taskToSave = Task(
                        id = task?.id ?: 0,
                        title = state.title,
                        description = state.description,
                        date = state.date,
                        time = state.time,
                        repeatType = state.repeatType,
                        extraTimes = state.extraTimes,
                        repeatDays = state.weeklyDays.toList(),

                        // Для MONTHLY используем день месяца
                        dayOfMonth =
                            if (state.repeatType == RepeatType.MONTHLY)
                                state.dayOfMonth ?: state.date.dayOfMonth
                            else null,
                        // Для COURSE используем длительность курса
                        courseDays =
                            if (state.repeatType == RepeatType.COURSE)
                                state.courseDays
                            else null,
                        subtasks = state.subtasks,
                        remindAt = remindAt,
                        soundUri = state.soundUri,
                        reminderOffset = state.reminderOffset
                    )

                    // Отправляем Intent во ViewModel
                    if (isNew) {
                        viewModel.processIntent(DayIntent.AddTask(taskToSave))
                    } else {
                        viewModel.processIntent(DayIntent.UpdateTask(taskToSave))
                    }
                    // Возвращаемся назад после сохранения
                    navController.popBackStack()
                }
            }
        }

    ) { padding ->
        // Основной контент формы редактирования задачи.
        //  Все поля работают по единому принципу:
        //  UI - onChange - обновление state через copy()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                // выбор типа повторения задачи
                RepeatTypeSelector(
                    repeatType = state.repeatType
                ) {
                    state = state.copy(repeatType = it)
                }
            }
            // ввод названия задачи
            item {
                TitleField(
                    state
                ) {
                    state = state.copy(title = it)
                }
            }

            item {
                // ввод описания задачи
                DescriptionField(
                    state
                ) {
                    state = state.copy(description = it)
                }
            }

            item {
                // блок выбора даты, времени и параметров курса
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // выбор даты (или дня месяца для MONTHLY)
                    DatePickerField(
                        state = state,
                        modifier = Modifier.weight(1.1f),
                        onChange = {
                            state = state.copy(date = it)
                        },
                        onDayOfMonthChange = {
                            state = state.copy(dayOfMonth = it)
                        }
                    )
                    // выбор времени задачи
                    TimePickerField(
                        state = state,
                        modifier = Modifier.weight(1f)
                    ) {
                        state = state.copy(time = it)
                    }

                    // поле длительности курса (только для COURSE)
                    if (state.repeatType == RepeatType.COURSE) {
                        CourseDaysField(
                            days = state.courseDays,
                            modifier = Modifier.weight(0.7f)
                        ) {
                            state = state.copy(courseDays = it)
                        }
                    }
                }
            }

            item {
                RepeatExtraFields(
                    state = state,
                    onUpdate = { state = it }
                )
            }
            item {
                // дополнительные параметры повторения (например WEEKLY)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // дополнительные времена для режима COURSE
                    if (state.repeatType == RepeatType.COURSE) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Дополнительное время
                            Checkbox(
                                checked = state.extraTimes.isNotEmpty(),
                                onCheckedChange = { enabled ->
                                    state = if (enabled) {
                                        state.copy(extraTimes = listOf(LocalTime.now()))
                                    } else {
                                        state.copy(extraTimes = emptyList())
                                    }
                                }
                            )
                            Text("Extra time")
                        }
                        // Отображаем блок дополнительных времён,
                        // если пользователь включил их (список не пуст)
                        if (state.extraTimes.isNotEmpty()) {
                            ExtraTimesBlock(
                                times = state.extraTimes,
                                onChange = {
                                    state = state.copy(extraTimes = it)
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        // Левая часть (чекбокс + текст)
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            // Включение/выключение напоминания
                            Checkbox(
                                checked = state.reminderOffset != null,
                                onCheckedChange = { enabled ->
                                    state = state.copy(
                                        reminderOffset = if (enabled)
                                            state.reminderOffset ?: ReminderOffset(0, 0, 0)
                                        else null
                                    )
                                }
                            )
                            Text("Напоминание")
                        }

                        // Выбор звука уведомления
                        if (state.reminderOffset != null) {
                            IconButton(
                                onClick = {
                                    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                                        putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Выберите звук")
                                    }

                                    launcher.launch(intent)
                                }
                            ) {
                                Icon(
                                    imageVector = if (state.soundUri != null)
                                        Icons.Default.NotificationsActive
                                    else
                                        Icons.Default.Notifications,
                                    contentDescription = "Выбрать звук"
                                )
                            }
                        }
                    }
                    // Настройка времени напоминания (offset)
                    if (state.reminderOffset != null) {
                        ReminderBlock(
                            offset = state.reminderOffset
                        ) {
                            state = state.copy(reminderOffset = it)
                        }
                    }
                }
            }

            item {
                // Управление подзадачами
                SubtasksEditor(
                    state = state,
                    onUpdate = { state = it }
                )
            }

            item {
                // Нижний отступ, чтобы контент не перекрывался кнопкой
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Показ ошибок через Toast
        showError?.let { message ->
            LaunchedEffect(message) {
                Toast
                    .makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
                showError = null
            }
        }
    }
}


  // Рассчитывает offset (разницу) между временем задачи и временем напоминания
fun calculateOffset(
    date: LocalDate,
    time: LocalTime?,
    remindAt: LocalDateTime?
): ReminderOffset? {

    // Если нет времени или напоминания — offset невозможен
    if (time == null || remindAt == null) return null

    val taskDateTime = LocalDateTime.of(date, time)

    val diff = Duration.between(remindAt, taskDateTime)

    val totalMinutes = diff.toMinutes().toInt()

    val days = totalMinutes / (24 * 60)
    val hours = (totalMinutes % (24 * 60)) / 60
    val minutes = totalMinutes % 60

    return ReminderOffset(
        days = days,
        hours = hours,
        minutes = minutes
    )
}


 // Рассчитывает время напоминания на основе offset
 fun calculateRemindAt(
    date: LocalDate,
    time: LocalTime?,
    offset: ReminderOffset?
): LocalDateTime? {

    // Если нет offset или времени — напоминание не нужно
    if (offset == null) return null
    if (time == null) return null

    val dateTime = LocalDateTime.of(date, time)

    return dateTime
        .minusDays(offset.days.toLong())
        .minusHours(offset.hours.toLong())
        .minusMinutes(offset.minutes.toLong())
}