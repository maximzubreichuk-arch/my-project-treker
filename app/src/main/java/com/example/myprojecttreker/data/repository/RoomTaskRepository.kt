package com.example.myprojecttreker.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.myprojecttreker.data.local.dao.SubTaskDao
import com.example.myprojecttreker.data.local.dao.TaskDao
import com.example.myprojecttreker.data.mapper.toDomain
import com.example.myprojecttreker.data.mapper.toEntity
import com.example.myprojecttreker.data.reminder.scheduler.ReminderScheduler
import com.example.myprojecttreker.domain.model.Task
import com.example.myprojecttreker.domain.repository.TaskRepository
import java.time.LocalDate


/**
 * Реализация TaskRepository через Room.
 *
 * Отвечает за:
 * - сохранение задач в БД
 * - работу с подзадачами
 * - управление напоминаниями
 */
@RequiresApi(Build.VERSION_CODES.O)
class RoomTaskRepository(
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao,
    private val reminderScheduler: ReminderScheduler
) : TaskRepository {



     // Добавляет новую задачу:
     // - сохраняет в БД
     // - планирует напоминание
     // - сохраняет подзадачи
    override suspend fun insertTask(task: Task) {

        // Сохраняем задачу и получаем её ID
        val taskId = taskDao.insert(task.toEntity())
        // Планируем напоминание (с новым ID)
        reminderScheduler.schedule(
            task.copy(id = taskId)
        )
        // Преобразуем подзадачи в Entity с правильным порядком
        val subtasks = task.subtasks.mapIndexed { index, subTask ->
            subTask.toEntity(taskId, index)
        }
        // Сохраняем подзадачи
        subTaskDao.insertAll(subtasks)
    }


     // Добавляем список задач
     // Используется для массового добавления (например импорт)
    override suspend fun insertAll(tasks: List<Task>) {
        tasks.forEach {
            insertTask(it)
        }
    }


     // Обновляет задачу:
     // - отменяет старые напоминания
     // - обновляет задачу
     //- пересоздаёт подзадачи
     // - планирует новые напоминания

    override suspend fun updateTask(task: Task) {
        // Отменяем старые напоминания
        reminderScheduler.cancel(task)
        // Обновляем задачу
        taskDao.update(
            task.toEntity()
        )
        // Удаляем старые подзадачи
        subTaskDao.deleteByTaskId(task.id)

        // Пересоздаём подзадачи с новым порядком
        val subtasks = task.subtasks.mapIndexed { index, subTask ->
            subTask.toEntity(
                taskId = task.id,
                position = index
            )
        }
        // Сохраняем все подзадачи в БД
        subTaskDao.insertAll(subtasks)

        // Планируем новые напоминания
        reminderScheduler.schedule(task)
    }


     // Удаляет задачу:
     // - отменяет напоминания
     // - удаляет запись из БД
    override suspend fun deleteTask(task: Task) {
        // Отменяем все связанные напоминания
        reminderScheduler.cancel(task)
        // Удаляем задачу из БД
        taskDao.delete(task.toEntity())
    }


      // Возвращает все задачи с подзадачами
    override suspend fun getAllTasks(): List<Task> {
        return taskDao
            .getAllWithSubtasks()
            // Преобразуем из Entity в доменную модель
            .map { it.toDomain() }
    }
}
