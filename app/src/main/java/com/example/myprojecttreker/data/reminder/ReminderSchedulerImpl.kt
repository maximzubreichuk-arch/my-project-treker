package com.example.myprojecttreker.data.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.myprojecttreker.data.local.AppDatabase
import com.example.myprojecttreker.data.mapper.toDomain
import com.example.myprojecttreker.domain.Task
import java.time.Duration
import java.time.LocalDateTime

/**
 * Реализация планировщика через AlarmManager.
 *
 * Для каждого временного слота задачи существует отдельный PendingIntent.
 * После срабатывания ReminderReceiver повторно рассчитывает ближайшее событие.
 */
class ReminderSchedulerImpl(
    private val context: Context
) : ReminderScheduler {

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun schedule(task: Task) {
        if (task.id == 0L || task.time == null) return

        val offset = resolveOffset(task) ?: return
        val now = LocalDateTime.now()
        val times = listOf(task.time) + task.extraTimes

        times.forEachIndexed { index, time ->
            val reminderAt = findNextReminder(task, time, offset, now) ?: return@forEachIndexed
            scheduleAlarm(task, index, reminderAt)
        }
    }

    override fun cancel(task: Task) {
        if (task.id == 0L) return

        val slotCount = 1 + task.extraTimes.size
        repeat(slotCount) { index ->
            alarmManager.cancel(pendingIntent(task.id, index))
        }
    }

    override suspend fun rescheduleAll() {
        val db = AppDatabase.getInstance(context)
        val tasks = db.taskDao()
            .getAllWithSubtasks()
            .map { it.toDomain() }

        tasks.forEach { task ->
            cancel(task)
            schedule(task)
        }
    }

    private fun resolveOffset(task: Task): Duration? {
        task.reminderOffset?.let {
            return Duration.ofDays(it.days.toLong())
                .plusHours(it.hours.toLong())
                .plusMinutes(it.minutes.toLong())
        }

        val taskTime = task.time ?: return null
        val remindAt = task.remindAt ?: return null
        val taskDateTime = LocalDateTime.of(task.date, taskTime)
        return Duration.between(remindAt, taskDateTime)
    }

    private fun findNextReminder(
        task: Task,
        slotTime: java.time.LocalTime,
        offset: Duration,
        now: LocalDateTime
    ): LocalDateTime? {
        val result = ReminderTimeCalculator.findNextReminder(task, slotTime, offset, now)
        if (result == null) {
            Log.w(TAG, "Could not find a future reminder for taskId=${task.id}, slot=$slotTime")
        }
        return result
    }

    private fun scheduleAlarm(task: Task, slotIndex: Int, triggerAt: LocalDateTime) {
        val triggerMillis = triggerAt
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        if (triggerMillis <= System.currentTimeMillis()) return

        val pendingIntent = pendingIntent(task.id, slotIndex)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            } else {
                // Грейсфул fallback: приложение продолжает работать и без special access,
                // но точность доставки в таком случае ниже.
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
                Log.w(TAG, "Exact alarm access unavailable, scheduled inexact alarm for taskId=${task.id}")
            }
        } catch (securityException: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
            Log.e(TAG, "Exact alarm scheduling denied for taskId=${task.id}; fallback to inexact alarm", securityException)
        }
    }

    private fun pendingIntent(taskId: Long, slotIndex: Int): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ReminderReceiver.ACTION_REMINDER
            data = ReminderReceiver.buildUri(taskId, slotIndex)
            putExtra(ReminderReceiver.EXTRA_TASK_ID, taskId)
            putExtra(ReminderReceiver.EXTRA_SLOT_INDEX, slotIndex)
        }

        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }


    companion object {
        private const val TAG = "ReminderScheduler"
    }
}
