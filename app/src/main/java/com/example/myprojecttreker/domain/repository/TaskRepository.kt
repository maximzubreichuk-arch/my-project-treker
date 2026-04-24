package com.example.myprojecttreker.domain.repository

import com.example.myprojecttreker.domain.model.Task

/**
 * Интерфейс репозитория задач (domain слой)
 */
interface TaskRepository {

 // Добавить задачу
    suspend fun insertTask(task: Task)

 // Добавить список задач
    suspend fun insertAll(tasks: List<Task>)

 // Обновить задачу
    suspend fun updateTask(task: Task)

 // Удалить задачу
    suspend fun deleteTask(task: Task)

 // Получить все задачи
    suspend fun getAllTasks(): List<Task>

}