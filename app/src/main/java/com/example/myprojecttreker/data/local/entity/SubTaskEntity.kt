package com.example.myprojecttreker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity подзадачи для базы данных (Room).
 *
 * Связана с TaskEntity через внешний ключ (taskId).
 *
 * Отвечает за:
 * - хранение подзадач
 * - порядок отображения (position)
 * - состояние выполнения (isDone)
 */
@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            // ссылка на родительскую задачу
            parentColumns = ["id"],
            // поле в этой таблице
            childColumns = ["taskId"],
            // при удалении задачи удаляются и подзадачи
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    // ускоряет поиск подзадач по задаче
    indices = [Index("taskId")]
)
data class SubTaskEntity(
    @PrimaryKey(autoGenerate = true)
    // уникальный ID подзадачи
    val id: Long = 0,
    // ID родительской задачи
    val taskId: Long,
    // текст подзадачи
    val title: String,
    // выполнена или нет
    val isDone: Boolean,
    // порядок отображения внутри задачи
    val position: Int
)