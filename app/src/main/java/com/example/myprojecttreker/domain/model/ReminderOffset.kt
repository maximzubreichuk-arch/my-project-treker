package com.example.myprojecttreker.domain.model

/**
 * Смещение напоминания относительно времени задачи*
 */

data class ReminderOffset(
    // Дни
    val days: Int,
    // Часы
    val hours: Int,
    // Минуты
    val minutes: Int
)