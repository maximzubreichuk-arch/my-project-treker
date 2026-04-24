package com.example.myprojecttreker.presentation.ui.utils

import com.example.myprojecttreker.data.local.entity.DayResultEntity
import com.example.myprojecttreker.domain.model.Task
import com.example.myprojecttreker.presentation.ui.calendar.DayVisualState
import java.time.LocalDate

/**
 * Вычисляет визуальное состояние дня для календаря.
 *
 * Используется для:
 * - окраски дня (выполнен / сегодня / будущее / прошлое)
 * - отображения прогресса задач
 */
fun calculateDayState(
    date: LocalDate,
    today: LocalDate,
    tasks: List<Task>,
    dayResults: List<DayResultEntity>
): DayVisualState {

    // Отбираем задачи, которые должны отображаться в этот день
    val tasksForDay = tasks.filter { it.isScheduledFor(date) }

    // Берём результаты выполнения только для этой даты
    val resultsForDay = dayResults.filter { it.date == date }

    // Есть ли вообще задачи в этот день
    val hasTasks = tasksForDay.isNotEmpty()


     //  Разворачиваем задачи в "инстансы":
     // одна задача может иметь несколько времён (time + extraTimes)
     // В итоге получаем пары:
     // (taskId, time)
    val taskInstances = tasksForDay.flatMap { task ->
        val times = buildList {

            // Основное время задачи
            task.time?.let { add(it) }

            // Дополнительные времена
            addAll(task.extraTimes)
        }

        // Если у задачи нет времени — считаем её как один инстанс без времени
        if (times.isEmpty()) {
            listOf(task.id to null)
        } else {
            // Иначе создаём инстанс на каждое время
            times.map { time -> task.id to time }
        }
    }



        //  Проверяем: выполнены ли ВСЕ инстансы задач за день
        val isCompleted =
        taskInstances.isNotEmpty() &&
                taskInstances.all { (taskId, time) ->

                    // Ищем результат выполнения для конкретной задачи и времени
                    val result = resultsForDay.find {

                        // time может быть null → тогда сохраняется строка "null"
                        it.taskId == taskId && it.time == (time?.toString() ?: "null")
                    }

                    // Задача считается выполненной, если найден result и он отмечен как done
                    result?.isDone == true
                }

    // Есть ли задачи в будущем (дата позже сегодняшней)
    val hasFutureTasks =
        hasTasks && date.isAfter(today)

    // Есть ли незавершённые задачи в прошлом
    val hasUnfinishedPast =
        hasTasks && date.isBefore(today) &&
                (resultsForDay.isEmpty() || resultsForDay.any { !it.isDone })


    // Определяем итоговое визуальное состояние дня
    return when {
        // Сегодняшний день
        date == today -> DayVisualState.Today
        // Все задачи выполнены
        isCompleted -> DayVisualState.Completed
        // Будущие задачи
        hasFutureTasks -> DayVisualState.Future
        // Прошлые незавершённые задачи
        hasUnfinishedPast -> DayVisualState.Past
        // Пустой день (нет задач)
        else -> DayVisualState.Empty
    }
}