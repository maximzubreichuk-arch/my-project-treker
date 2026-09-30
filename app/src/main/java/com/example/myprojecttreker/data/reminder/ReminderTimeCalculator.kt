package com.example.myprojecttreker.data.reminder

import com.example.myprojecttreker.domain.Task
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Чистая логика поиска ближайшего времени напоминания.
 * Не зависит от Android API, поэтому легко тестируется.
 */
object ReminderTimeCalculator {

    private const val MAX_OCCURRENCE_SEARCH = 4096

    fun findNextReminder(
        task: Task,
        slotTime: LocalTime,
        offset: Duration,
        now: LocalDateTime
    ): LocalDateTime? {
        var cursor = now.minusNanos(1)

        repeat(MAX_OCCURRENCE_SEARCH) {
            val occurrence = task.copy(time = slotTime).getNextOccurrenceDateTime(cursor)
                ?: return null
            val reminderAt = occurrence.minus(offset)

            if (reminderAt.isAfter(now)) return reminderAt
            cursor = occurrence
        }

        return null
    }
}
