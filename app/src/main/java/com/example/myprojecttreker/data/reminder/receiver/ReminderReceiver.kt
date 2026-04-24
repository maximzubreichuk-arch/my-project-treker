package com.example.myprojecttreker.data.reminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.myprojecttreker.data.local.database.AppDatabase
import com.example.myprojecttreker.data.mapper.toDomain
import com.example.myprojecttreker.data.reminder.scheduler.ReminderSchedulerImpl
import com.example.myprojecttreker.domain.model.RepeatType
import com.example.myprojecttreker.system.service.ReminderService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver, который срабатывает в момент напоминания.
 *
 * Задачи:
 * 1. Запустить сервис уведомления (звук + нотификация)
 * 2. Если задача повторяющаяся — запланировать следующее напоминание
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.e("REMINDER_TEST", "ALARM FIRED")

        // 1. Запускаем сервис, который покажет уведомление
        val serviceIntent = Intent(context, ReminderService::class.java).apply {
            putExtra("title", intent.getStringExtra("title"))
            putExtra("soundUri", intent.getStringExtra("soundUri"))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

         // goAsync() нужен, чтобы receiver не завершился
         // до окончания фоновой работы (корутины)
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val taskId = intent.getLongExtra("taskId", 0L)
                val db = AppDatabase.Companion.getInstance(context)
                val taskWithSubtasks =
                    db.taskDao().getTaskWithSubTasks(taskId)
                val task = taskWithSubtasks?.toDomain() ?: return@launch

                 // Если задача одноразовая — ничего не планируем
                if (task.repeatType == RepeatType.ONCE) return@launch
                // Рассчитываем следующую дату выполнения
                val nextTask = task.copy(
                    date = task.getNextOccurrenceDateTime().toLocalDate()
                )

                // Планируем следующее напоминание
                val scheduler = ReminderSchedulerImpl(context)
                scheduler.schedule(nextTask)
            } finally {
                // Обязательно завершить pendingResult
                pendingResult.finish()
            }
        }
    }
}