package com.example.myprojecttreker.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth

/**
 * Основная доменная модель задачи.
 */
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: LocalDate,
    val time: LocalTime? = null,
    val isDone: Boolean = false,
    val priority: Int = 1,
    val remindAt: LocalDateTime? = null,
    val repeatType: RepeatType = RepeatType.ONCE,
    val repeatDays: List<DaysOfWeek> = emptyList(),
    val courseDays: Int? = null,
    val subtasks: List<SubTask> = emptyList(),
    val dayOfMonth: Int? = null,
    val isExpanded: Boolean = false,
    val extraTimes: List<LocalTime> = emptyList(),
    val soundUri: String? = null,
    val reminderOffset: ReminderOffset? = null,
    val iconId: String = "default"
) {

    fun isScheduledFor(date: LocalDate): Boolean {
        return when (repeatType) {
            RepeatType.ONCE -> this.date == date
            RepeatType.DAILY -> !date.isBefore(this.date)
            RepeatType.WEEKLY ->
                !date.isBefore(this.date) &&
                        repeatDays.contains(DaysOfWeek.valueOf(date.dayOfWeek.name))
            RepeatType.MONTHLY -> {
                val targetDay = dayOfMonth ?: this.date.dayOfMonth
                val safeDay = minOf(targetDay, date.lengthOfMonth())
                !date.isBefore(this.date) && date.dayOfMonth == safeDay
            }
            RepeatType.YEARLY -> {
                val targetDay = this.date.dayOfMonth
                val safeDay = minOf(targetDay, date.lengthOfMonth())
                !date.isBefore(this.date) &&
                        date.dayOfMonth == safeDay &&
                        date.month == this.date.month
            }
            RepeatType.COURSE ->
                !date.isBefore(this.date) &&
                        date.isBefore(this.date.plusDays((courseDays ?: 1).toLong()))
        }
    }

    /**
     * Возвращает ближайшее время выполнения задачи строго после [from].
     * Null означает, что активного будущего выполнения нет.
     */
    fun getNextOccurrenceDateTime(
        from: LocalDateTime = LocalDateTime.now()
    ): LocalDateTime? {
        val taskTime = time ?: return null

        return when (repeatType) {
            RepeatType.ONCE -> {
                val candidate = LocalDateTime.of(date, taskTime)
                candidate.takeIf { it.isAfter(from) }
            }

            RepeatType.DAILY -> {
                val startDate = maxOf(date, from.toLocalDate())
                var candidate = LocalDateTime.of(startDate, taskTime)
                if (!candidate.isAfter(from)) candidate = candidate.plusDays(1)
                candidate
            }

            RepeatType.WEEKLY -> {
                if (repeatDays.isEmpty()) return null
                val targetDays = repeatDays.map { DayOfWeek.valueOf(it.name) }.toSet()
                val startDate = maxOf(date, from.toLocalDate())

                for (i in 0..14) {
                    val candidateDate = startDate.plusDays(i.toLong())
                    if (candidateDate.dayOfWeek !in targetDays) continue
                    val candidate = LocalDateTime.of(candidateDate, taskTime)
                    if (candidate.isAfter(from)) return candidate
                }
                null
            }

            RepeatType.MONTHLY -> {
                val targetDay = dayOfMonth ?: date.dayOfMonth
                val firstMonth = YearMonth.from(maxOf(date, from.toLocalDate()))

                for (i in 0..24) {
                    val month = firstMonth.plusMonths(i.toLong())
                    val safeDay = minOf(targetDay, month.lengthOfMonth())
                    val candidate = LocalDateTime.of(month.atDay(safeDay), taskTime)
                    if (!candidate.toLocalDate().isBefore(date) && candidate.isAfter(from)) {
                        return candidate
                    }
                }
                null
            }

            RepeatType.YEARLY -> {
                val firstYear = maxOf(date, from.toLocalDate()).year
                val targetMonth = date.month
                val targetDay = date.dayOfMonth

                for (i in 0..10) {
                    val year = firstYear + i
                    val month = targetMonth
                    val safeDay = minOf(targetDay, YearMonth.of(year, month).lengthOfMonth())
                    val candidate = LocalDateTime.of(
                        LocalDate.of(year, month, safeDay),
                        taskTime
                    )
                    if (!candidate.toLocalDate().isBefore(date) && candidate.isAfter(from)) {
                        return candidate
                    }
                }
                null
            }

            RepeatType.COURSE -> {
                val duration = courseDays ?: 0
                if (duration <= 0) return null
                val endDate = date.plusDays(duration.toLong())
                var candidateDate = maxOf(date, from.toLocalDate())

                while (candidateDate.isBefore(endDate)) {
                    val candidate = LocalDateTime.of(candidateDate, taskTime)
                    if (candidate.isAfter(from)) return candidate
                    candidateDate = candidateDate.plusDays(1)
                }
                null
            }
        }
    }
}
