package com.example.myprojecttreker.data.reminder

import com.example.myprojecttreker.domain.Task

/**
 * Планировщик пользовательских напоминаний.
 *
 * Напоминания выполняются через AlarmManager, а не через WorkManager,
 * потому что пользователю нужен запуск максимально близко к заданному времени.
 */
interface ReminderScheduler {
    fun schedule(task: Task)
    fun cancel(task: Task)
    suspend fun rescheduleAll()
}
