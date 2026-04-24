package com.example.myprojecttreker.presentation.viewmodel

import com.example.myprojecttreker.domain.model.Task

/**
 * События (Event) для Day экрана.
 *
 * Используются для передачи действий пользователя во ViewModel.
 *
 * Отвечают за:
 * - навигацию по дням
 * - обновление состояния задач
 * - повторную загрузку данных
 *
 * Особенности:
 * - реализуют MVI-подход (UI → Event → ViewModel)
 * - являются источником пользовательских действий
 */
sealed class DayEvent {

    // Инициализация экрана (первичная загрузка данных)
    object Init : DayEvent()

    // Переход к следующему дню
    object NextDay : DayEvent()

    // Переход к предыдущему дню
    object PreviousDay : DayEvent()

    // Повторная попытка загрузки (например, после ошибки)
    object Retry : DayEvent()

     //  Событие изменения состояния задачи (выполнена / не выполнена).
     // @param task Задача, которую изменили
     // @param isDone Новое состояние выполнения
    data class TaskChecked(
         val task: Task,
         val isDone: Boolean
    ) : DayEvent()
}