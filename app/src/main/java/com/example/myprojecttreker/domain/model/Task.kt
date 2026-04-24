package com.example.myprojecttreker.domain.model

import com.example.myprojecttreker.domain.model.DaysOfWeek
import com.example.myprojecttreker.domain.model.ReminderOffset
import com.example.myprojecttreker.domain.model.RepeatType
import com.example.myprojecttreker.domain.model.SubTask
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Основная доменная модель задачи
 */
data class Task(
    // Уникальный идентификатор
    val id: Long = 0,
    // Название задачи
    val title: String,
    // Описание задачи
    val description: String = "",
    // Дата выполнения
    val date: LocalDate,
    // Время выполнения (может отсутствовать)
    val time: LocalTime? = null,
    // Выполнена ли задача
    val isDone: Boolean = false,
    // Приоритет задачи
    val priority: Int = 1,
    // Время напоминания
    val remindAt: LocalDateTime? = null,
    // Тип повторения
    val repeatType: RepeatType = RepeatType.ONCE,
    // Дни недели (для WEEKLY)
    val repeatDays: List<DaysOfWeek> = emptyList(),
    // Длительность курса
    val courseDays: Int? = null,
    // Подзадачи
    val subtasks: List<SubTask> = emptyList(),
    // День месяца (для MONTHLY)
    val dayOfMonth: Int? = null,
    // Развернута ли задача в UI
    val isExpanded: Boolean = false,
    // Дополнительные времена
    val extraTimes: List<LocalTime> = emptyList(),
    // URI звука
    val soundUri: String? = null,
    // Смещение напоминания
    val reminderOffset: ReminderOffset? = null

) {

    // Проверяет, должна ли задача отображаться в конкретный день
    fun isScheduledFor(date: LocalDate): Boolean {
        return when (repeatType) {

            // Одноразовая задача
            RepeatType.ONCE ->
                this.date == date

            // Ежедневная задача
            RepeatType.DAILY ->
                !date.isBefore(this.date)

            // Еженедельная задача
            RepeatType.WEEKLY ->
                !date.isBefore(this.date) &&
                        repeatDays.contains(
                            DaysOfWeek.valueOf(date.dayOfWeek.name)
                        )

            // Ежемесячная задача
            RepeatType.MONTHLY -> {
                val targetDay = this.dayOfMonth ?: this.date.dayOfMonth
                val safeDay = minOf(targetDay, date.lengthOfMonth())

                !date.isBefore(this.date) &&
                        date.dayOfMonth == safeDay
            }

            // Ежегодная задача
            RepeatType.YEARLY ->
                !date.isBefore(this.date) &&
                        date.dayOfMonth == this.date.dayOfMonth &&
                        date.month == this.date.month

            // Курс (несколько дней подряд)
            RepeatType.COURSE ->
                !date.isBefore(this.date) &&
                        date.isBefore(
                            this.date.plusDays((courseDays ?: 1).toLong())
                        )
        }
    }


    // Рассчитывает следующую дату выполнения задачи
    fun getNextOccurrenceDateTime(
        from: LocalDateTime = LocalDateTime.now()
    ): LocalDateTime {

        val taskTime = time ?: LocalTime.MIN

        return when (repeatType) {

              // Одноразовая
            RepeatType.ONCE -> {
                LocalDateTime.of(date, taskTime)
            }
              // Ежедневная
            RepeatType.DAILY -> {
                val todayDateTime = LocalDateTime.of(from.toLocalDate(), taskTime)

                if (todayDateTime.isAfter(from)) {
                    todayDateTime
                } else {
                    todayDateTime.plusDays(1)
                }
            }
              // Еженедельная
            RepeatType.WEEKLY -> {

                val targetDays = repeatDays.map {
                    DayOfWeek.valueOf(it.name)
                }

                // ищем ближайший подходящий день
                for (i in 0..7) {
                    val candidateDate = from.toLocalDate().plusDays(i.toLong())
                    val candidateDateTime = LocalDateTime.of(candidateDate, taskTime)

                    if (
                        targetDays.contains(candidateDate.dayOfWeek) &&
                        candidateDateTime.isAfter(from)
                    ) {
                        return candidateDateTime
                    }
                }

                // через неделю
                val nextWeek = from.toLocalDate().plusWeeks(1)
                return LocalDateTime.of(nextWeek, taskTime)
            }

            // Остальные типы пока используют базовую дату
            else -> LocalDateTime.of(date, taskTime)
        }
    }
}