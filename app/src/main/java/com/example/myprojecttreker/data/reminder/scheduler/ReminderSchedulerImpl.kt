package com.example.myprojecttreker.data.reminder.scheduler

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.myprojecttreker.domain.model.Task
import com.example.myprojecttreker.system.worker.ReminderWorker
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Реализация планировщика напоминаний через WorkManager.
 * Отвечает за создание и отмену задач напоминаний.
 */
class ReminderSchedulerImpl(
    private val context: Context
) : ReminderScheduler {


    // Планирует напоминание для задачи.
    // Учитывает основное время и дополнительные (extraTimes).
    override fun schedule(task: Task) {

        // Если время задачи не указано — нечего планировать
        val taskTime = task.time ?: return
        // базовое время задачи
        val baseDateTime = LocalDateTime.of(task.date, taskTime)
        // Время напоминания
        val remindAt = task.remindAt ?: return
        // вычисляем смещение
        val offset = Duration.between(remindAt, baseDateTime)
        // Список всех напоминаний (основное + дополнительные)
        val times = mutableListOf<Pair<Int, LocalDateTime>>()

        // Основное напоминание
        times.add(0 to baseDateTime.minus(offset))

        // дополнительные времена(extraTimes)
        task.extraTimes.forEachIndexed { i, time ->
            val dateTime = LocalDateTime.of(task.date, time)
            times.add((i + 1) to dateTime.minus(offset))
        }
        // Планируем каждое напоминание
        times.forEach { (index, remindAt) ->
            //  Вычисляем задержку до выполнения
            val delay = Duration.between(
                LocalDateTime.now(),
                remindAt
            ).toMillis()

            // если время прошло - пропускаем
            if (delay <= 0) return@forEach
            // Данные для Worker
            val data = workDataOf(
                "title" to task.title,
                "soundUri" to task.soundUri
            )
            // Создание задачи WorkManager
            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            // уникальный work для каждой задачи
            WorkManager.Companion.getInstance(context)
                .enqueueUniqueWork(
                    "task_${task.id}_$index",
                    ExistingWorkPolicy.REPLACE,
                    request
                )
        }
    }

    // Отменяет все запланированные напоминания для задачи
    override fun cancel(task: Task) {

        val wm = WorkManager.Companion.getInstance(context)

        // Количество всех возможных напоминаний
        val total = 1 + task.extraTimes.size

        // отменяем все возможные напоминани
        for (i in 0 until total) {
            wm.cancelUniqueWork("task_${task.id}_$i")
        }
    }
}