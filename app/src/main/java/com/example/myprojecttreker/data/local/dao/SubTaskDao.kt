package com.example.myprojecttreker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.myprojecttreker.data.local.entity.SubTaskEntity

/**
 * DAO для работы с подзадачами
 */
@Dao
interface SubTaskDao {

    // Добавить подзадачу
    @Insert
    suspend fun insert(subTask: SubTaskEntity)

    // Добавить список подзадач
    @Insert
    suspend fun insertAll(subTasks: List<SubTaskEntity>)

    // Обновить подзадачу
    @Update
    suspend fun update(subTask: SubTaskEntity)

    // Удалить подзадачу
    @Delete
    suspend fun delete(subTask: SubTaskEntity)

    //Получить подзадачи по задаче
    @Query(
        """
        SELECT * FROM subtasks
        WHERE taskId = :taskId
        ORDER BY position ASC
        """
    )
    suspend fun getByTask(taskId: Long): List<SubTaskEntity>

    // Удалить все подзадачи задачи
    @Query(
        "DELETE FROM subtasks WHERE taskId = :taskId"
    )
    suspend fun deleteByTaskId(taskId: Long)
}