package com.example.myprojecttreker.domain.model

/**
 * Доменная модель подзадачи
 */
data class SubTask(
    // ID подзадачи
    val id: Long,
    // Название
    val title: String,
    // Выполнена ли
    val isDone: Boolean,
)