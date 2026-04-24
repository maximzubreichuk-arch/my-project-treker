package com.example.myprojecttreker.presentation.viewmodel

import com.example.myprojecttreker.domain.model.SubTask
import com.example.myprojecttreker.domain.model.Task
import java.time.LocalDate

/**
 * Intents (намерения пользователя) для экрана "День".
 *
 * Используются как входные события в ViewModel.
 * ViewModel обрабатывает их и обновляет DayUiState.
 *
 * Это часть MVI-подхода:
 * UI → Intent → ViewModel → State → UI
 */
sealed class DayIntent {

    // Инициализация экрана (первичная загрузка)
    object Init : DayIntent()

    // Переход к следующему дню
    object NextDay : DayIntent()

    // Переход к предыдущему дню
    object PreviousDay : DayIntent()

    // Повторная попытка загрузки (после ошибки)
    object Retry : DayIntent()

    // Отмена удаления (зарезервировано под future use)
    object UndoDelete : DayIntent()

    // Переход к следующему месяцу
    object NextMonth : DayIntent()

    // Переход к предыдущему месяцу
    object PreviousMonth : DayIntent()


    // Выбор конкретной даты
    data class SelectDate(val date: LocalDate) : DayIntent()

    // Изменение состояния выполнения задачи (чекбокс)
    data class TaskChecked(
        val task: Task,
        val isDone: Boolean
    ) : DayIntent()

    // Добавление новой задачи
    data class AddTask(
        val task: Task
    ) : DayIntent()

    // Удаление задачи
    data class DeleteTask(
        val task: Task
    ) : DayIntent()

    // Обновление существующей задачи
    data class UpdateTask(
        val task: Task
    ) : DayIntent()

    // Переключение раскрытия (expand/collapse) задачи
    data class ToggleExpand(
        val task: Task
    ) : DayIntent()


     // Переключение результата выполнения задачи за день.
     //  Используется для:
     //  - чекбокса задачи
     //  - чекбоксов подзадач
     //  - задач с несколькими временами (time / extraTimes)
    data class ToggleDayResult(
         // ID задачи
         val taskId: Long,
         // дата выполнения
         val date: LocalDate,
         // ключ времени (может быть "null")
         val time: String,
         // выполнено или нет
         val isDone: Boolean,
         // текущее состояние подзадач
         val subtasks: List<SubTask>
    ) : DayIntent()
}