package com.example.myprojecttreker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Entity задачи для базы данных (Room).
 *
 * Хранит основную информацию о задаче,
 * включая повторения, напоминания и дополнительные времена.
 *
 * ВАЖНО:
 * Некоторые поля хранятся в сериализованном виде (String),
 * так как Room не умеет работать с коллекциями напрямую.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    // уникальный ID задачи
    val id: Long = 0,
    // название задачи
    val title: String,
    // описание
    val description: String,
    // базовая дата задачи
    val date: LocalDate,
    // основное время (может отсутствовать)
    val time: LocalTime?,
    // выполнена ли задача (общий флаг)
    val isDone: Boolean,
    // приоритет (используется для сортировки/логики)
    val priority: Int,
    // время напоминания
    val remindAt: LocalDateTime?,
    // тип повторения (enum хранится как String)
    val repeatType: String,
    // дни недели через запятую (MONDAY, TUESDAY...)
    val repeatDays: String,
    // день месяца (для MONTHLY)
    val dayOfMonth: Int?,
    // длительность курса (для COURSE)
    val courseDays: Int?,
    // дополнительные времена через запятую (HH:mm)
    val extraTimesJson: String,
    // URI звука уведомления
    val soundUri: String?
)