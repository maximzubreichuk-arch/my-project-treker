package com.example.myprojecttreker.data.mapper

import com.example.myprojecttreker.data.local.SubTaskEntity
import com.example.myprojecttreker.data.local.TaskEntity
import com.example.myprojecttreker.data.local.TaskWithSubTasks
import com.example.myprojecttreker.domain.DaysOfWeek
import com.example.myprojecttreker.domain.RepeatType
import com.example.myprojecttreker.domain.ReminderOffset
import com.example.myprojecttreker.domain.SubTask
import com.example.myprojecttreker.domain.Task
import java.time.LocalTime

/**
 * Мапперы для преобразования:
 * - Task <-> TaskEntity
 * - SubTask <-> SubTaskEntity
 *
 * Также содержит вспомогательные функции сериализации подзадач.
 */

// Преобразование доменной модели Task в сущность базы данных
fun Task.toEntity(): TaskEntity =
    TaskEntity(
        id = id,
        title = title,
        description = description,
        date = date,
        time = time,
        isDone = isDone,
        priority = priority,
        remindAt = remindAt,
        // enum -> String (Room не умеет хранить enum напрямую)
        repeatType = repeatType.name,
        // список дней -> строка (например: "MONDAY,TUESDAY")
        repeatDays = repeatDays.joinToString(","),
        dayOfMonth = dayOfMonth,
        courseDays = courseDays,
        // Дополнительные времена поддерживаются только для COURSE.
        extraTimesJson = if (repeatType == RepeatType.COURSE) {
            extraTimes.joinToString(",") { it.toString() }
        } else {
            ""
        },
        soundUri = soundUri,
        reminderOffsetDays = reminderOffset?.days,
        reminderOffsetHours = reminderOffset?.hours,
        reminderOffsetMinutes = reminderOffset?.minutes,
        iconId = iconId

    )

// Преобразование SubTask -> SubTaskEntity
fun SubTask.toEntity(
    taskId: Long,
    position: Int
): SubTaskEntity =
    SubTaskEntity(
        id = id,
        taskId = taskId,
        title = title,
        isDone = isDone,
        position = position
    )

// Преобразование связки Task + SubTasks из БД в доменную модель
fun TaskWithSubTasks.toDomain(): Task {
    val repeatType = RepeatType.valueOf(task.repeatType)
    val extraTimes = if (repeatType == RepeatType.COURSE && task.extraTimesJson.isNotEmpty()) {
        task.extraTimesJson.split(",").map { LocalTime.parse(it) }
    } else {
        emptyList()
    }

    return Task(
        id = task.id,
        title = task.title,
        description = task.description,
        date = task.date,
        time = task.time,
        isDone = task.isDone,
        priority = task.priority,
        dayOfMonth = task.dayOfMonth,
        remindAt = task.remindAt,
        repeatType = repeatType,
        extraTimes = extraTimes,
        repeatDays =
            if (task.repeatDays.isEmpty()) {
                emptyList()
            } else {
                task.repeatDays.split(",").map { DaysOfWeek.valueOf(it) }
            },
        courseDays = task.courseDays,
        subtasks =
            subtasks
                .sortedBy { it.position }
                .map { it.toDomain() },
        soundUri = task.soundUri,
        reminderOffset = if (
            task.reminderOffsetDays != null &&
            task.reminderOffsetHours != null &&
            task.reminderOffsetMinutes != null
        ) {
            ReminderOffset(
                days = task.reminderOffsetDays,
                hours = task.reminderOffsetHours,
                minutes = task.reminderOffsetMinutes
            )
        } else {
            null
        },
        iconId = task.iconId
    )
}


// Преобразование SubTaskEntity -> SubTask
fun SubTaskEntity.toDomain(): SubTask {
    return SubTask(
        id = id,
        title = title,
        isDone = isDone

    )
}

// Конвертация списка подзадач в строку (для хранения в БД)
fun List<SubTask>.toJson(): String =
    joinToString("|") {
        "${it.id};${it.title};${it.isDone}"
    }

// Конвертация строки обратно в список подзадач
fun String.toSubTasks(): List<SubTask> =
    if (isEmpty())
    // если строка пустая — подзадач нет
        emptyList()
    else split("|").map {
        val parts = it.split(";")
        SubTask(
            id = parts[0].toLong(),
            title = parts[1],
            isDone = parts[2].toBoolean()
        )
    }