package com.example.myprojecttreker.data.reminder.scheduler

import com.example.myprojecttreker.domain.model.Task

/**
 * Интерфейс планировщика напоминаний
 */
interface ReminderScheduler {

    // Запланировать напоминание для задачи
    fun schedule(task: Task)

    // Отменить напоминание
    fun cancel(task: Task)
}