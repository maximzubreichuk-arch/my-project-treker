package com.example.myprojecttreker.data.local

import androidx.room.*

/**
 * DAO для работы с задачами
 */
@Dao
interface TaskDao {

        // Получить задачу с подзадачами
        @Transaction
        @Query("SELECT * FROM tasks WHERE id = :taskId")
        suspend fun getTaskWithSubTasks(taskId: Long): TaskWithSubTasks?

        // Получить все задачи с подзадачами
        @Transaction
        @Query("SELECT * FROM tasks")
        suspend fun getAllWithSubtasks(): List<TaskWithSubTasks>

        // Добавить задачу
        @Insert
        suspend fun insert(task: TaskEntity): Long

        // Обновить задачу
        @Update
        suspend fun update(task: TaskEntity)

        // Удалить задачу
        @Delete
        suspend fun delete(task: TaskEntity)

        // Получить все задачи
        @Query("SELECT * FROM tasks")
        suspend fun getAllTasks(): List<TaskEntity>
    }
