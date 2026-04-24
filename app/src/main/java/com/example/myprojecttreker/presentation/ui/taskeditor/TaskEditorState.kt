package com.example.myprojecttreker.presentation.ui.taskeditor

import com.example.myprojecttreker.domain.model.DaysOfWeek
import com.example.myprojecttreker.domain.model.ReminderOffset
import com.example.myprojecttreker.domain.model.RepeatType
import com.example.myprojecttreker.domain.model.SubTask
import java.time.LocalDate
import java.time.LocalTime

/**
 * Состояние экрана создания/редактирования задачи.
 *
 * Используется в TaskEditorScreen для:
 * - хранения текущих значений полей
 * - управления формой
 * - подготовки данных перед сохранением
 */
data class TaskEditorState(
    // название задачи
    val title: String = "",
    // описание задачи
    val description: String = "",
    // тип повторения (один раз, ежедневно и т.д.)
    val repeatType: RepeatType = RepeatType.ONCE,
    // выбранная дата
    val date: LocalDate = LocalDate.now(),
    // время выполнения (может отсутствовать)
    val time: LocalTime? = null,
    // дни недели (для WEEKLY режима)
    val weeklyDays: Set<DaysOfWeek> = emptySet(),
    // день месяца (для MONTHLY режима)
    val dayOfMonth: Int? = null,
    // длительность курса (для COURSE режима)
    val courseDays: Int = 1,
    // список подзадач
    val subtasks: List<SubTask> = emptyList(),
    // смещение напоминания (за сколько до задачи)
    val reminderOffset: ReminderOffset? = null,
    // дополнительные времена (для COURSE)
    val extraTimes: List<LocalTime> = emptyList(),
    // выбранный звук уведомления
    val soundUri: String? = null
)