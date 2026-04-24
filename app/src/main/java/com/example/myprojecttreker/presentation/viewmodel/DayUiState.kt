package com.example.myprojecttreker.presentation.viewmodel

import com.example.myprojecttreker.data.local.entity.DayResultEntity
import com.example.myprojecttreker.domain.model.Task
import java.time.LocalDate

/**
 * Состояние экрана "День".
 *
 * Используется ViewModel для управления UI через StateFlow.
 *
 * Описывает все возможные состояния экрана:
 * - загрузка данных
 * - пустой список
 * - отображение контента
 * - ошибка
 */
sealed class DayUiState {

    // Состояние загрузки данных для выбранной даты
    data class Loading(val date: LocalDate) : DayUiState()

    // Состояние, когда задач на выбранную дату нет
    data class Empty(val date: LocalDate) : DayUiState()
    data class Content(

        // выбранная дата
        val date: LocalDate,

        // список задач
        val tasks: List<Task>,

        // результаты выполнения (чекбоксы, подзадачи)
        val dayResults: List<DayResultEntity>
    ) : DayUiState()

    // Состояние ошибки (например, ошибка загрузки)
    data class Error(
        val date: LocalDate,

        // сообщение для отображения
        val message: String
    ) : DayUiState()
}