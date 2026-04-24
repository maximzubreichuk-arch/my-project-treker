package com.example.myprojecttreker.data.reminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myprojecttreker.data.local.database.AppDatabase
import com.example.myprojecttreker.data.mapper.toDomain
import com.example.myprojecttreker.data.reminder.scheduler.ReminderSchedulerImpl
import com.example.myprojecttreker.domain.model.RepeatType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

/**
 * BroadcastReceiver, который срабатывает после перезагрузки устройства.
 *
 * Задача:
 * восстановить все запланированные напоминания,
 * так как WorkManager/AlarmManager НЕ сохраняют их после reboot.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        // Нас интересует только событие завершения загрузки системы
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        // Работаем в фоне, чтобы не блокировать главный поток
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.Companion.getInstance(context)

            // Получаем все задачи с подзадачами
            val tasks = db.taskDao().getAllWithSubtasks()
            val scheduler = ReminderSchedulerImpl(context)
            val now = LocalDateTime.now()

            tasks.forEach { taskWithSubtasks ->
                val task = taskWithSubtasks.toDomain()

                // Если у задачи нет напоминания - пропускаем
                val remindAt = task.remindAt ?: return@forEach

                // СЛУЧАЙ 1: напоминание ещё не наступило
                if (remindAt.isAfter(now)) {
                    // Просто восстанавливаем
                    scheduler.schedule(task)
                } else {
                    // СЛУЧАЙ 2: напоминание уже прошло

                    // Если задача одноразовая - ничего не делаем
                    if (task.repeatType != RepeatType.ONCE) {
                        // Если задача повторяющаяся — считаем следующее выполнение
                        val taskTime = task.time ?: return@forEach
                        val nextDateTime = task.getNextOccurrenceDateTime()
                        // Исходное время задачи
                        val originalTaskDateTime =
                            LocalDateTime.of(task.date, taskTime)
                        // Смещение между remindAt и временем задачи
                        val offset = Duration.between(
                            task.remindAt,
                            originalTaskDateTime
                        )
                        // Вычисляем новое время напоминания
                        val nextRemindAt = nextDateTime.minus(offset)
                        // Планируем заново
                        scheduler.schedule(
                            task.copy(remindAt = nextRemindAt)
                        )
                    }
                }
            }
        }
    }
}