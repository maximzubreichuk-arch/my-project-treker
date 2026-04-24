package com.example.myprojecttreker.data.local.entity

import androidx.room.Entity
import java.time.LocalDate

/**
 * Сущность результата выполнения задачи за день
  */
@Entity(
    tableName = "day_results",
    primaryKeys = ["taskId", "date", "time"]
)
data class DayResultEntity(
    // ID задачи
    val taskId: Long,
    // Дата выполнения
    val date: LocalDate,
    //Время выполнения (ключ для multiple time)
    val time: String,
    // Выполнена ли задача
    val isDone: Boolean,
    // Подзадачи в JSON виде
    val subtasksJson: String

)