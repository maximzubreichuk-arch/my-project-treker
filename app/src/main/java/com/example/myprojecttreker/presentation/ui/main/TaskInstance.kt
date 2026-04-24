package com.example.myprojecttreker.presentation.ui.main

import com.example.myprojecttreker.domain.model.Task
import java.time.LocalTime

/**
 * Представление конкретного экземпляра задачи с учётом времени выполнения.
 *
 * Используется для:
 * - разворачивания одной задачи в несколько "инстансов"
 * - поддержки задач с несколькими временами (extraTimes)
 * - корректной отрисовки списка задач по времени
 *
 * Пример:
 * Одна задача → несколько TaskInstance (по каждому времени)
 *
 * Особенности:
 * - не является сущностью БД
 * - используется только на уровне UI (presentation layer)
 */
data class TaskInstance(

    // Сама задача
    val task: Task,

    // Конкретное время выполнения (может быть null)
    val time: LocalTime?
)