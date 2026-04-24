package com.example.myprojecttreker.data.mapper

import com.example.myprojecttreker.data.local.entity.SubTaskEntity
import com.example.myprojecttreker.data.local.entity.TaskEntity
import com.example.myprojecttreker.data.local.relation.TaskWithSubTasks
import com.example.myprojecttreker.domain.model.DaysOfWeek
import com.example.myprojecttreker.domain.model.RepeatType
import com.example.myprojecttreker.domain.model.SubTask
import com.example.myprojecttreker.domain.model.Task
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
        // список времени -> строка (например: "10:00,14:00")
        extraTimesJson = extraTimes.joinToString(",") { it.toString() },
        soundUri = soundUri

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

//Преобразование связки Task + SubTasks из БД в доменную модель
fun TaskWithSubTasks.toDomain(): Task =
    Task(
        id = task.id,
        title = task.title,
        description = task.description,
        date = task.date,
        time = task.time,
        isDone = task.isDone,
        priority = task.priority,
        dayOfMonth = task.dayOfMonth,
        remindAt = task.remindAt,
        // строка -> список времени
        extraTimes =
            if (task.extraTimesJson.isEmpty())
            // если строка пустая — дополнительных времен нет
                emptyList()
            else
                task.extraTimesJson.split(",").map { LocalTime.parse(it) },
        // строка -> enum
        repeatType = RepeatType.valueOf(task.repeatType),

        // строка -> список enum (дни недели)
        repeatDays =
            if (task.repeatDays.isEmpty())
            // если строка пустая — дополнительных времен нет
                emptyList()
            else
                task.repeatDays.split(",").map {
                    DaysOfWeek.valueOf(it)
                },

        courseDays = task.courseDays,

        // преобразование подзадач:
        // - сортируем по позиции
        // - конвертируем в доменную модель
        subtasks =
            subtasks
                .sortedBy { it.position }
                .map { it.toDomain() },

        soundUri = task.soundUri
    )


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