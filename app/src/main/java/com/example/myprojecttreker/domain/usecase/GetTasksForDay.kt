package com.example.myprojecttreker.domain.usecase

import com.example.myprojecttreker.domain.model.DaysOfWeek
import com.example.myprojecttreker.domain.model.RepeatType
import com.example.myprojecttreker.domain.model.Task
import com.example.myprojecttreker.domain.repository.TaskRepository
import java.time.LocalDate

/**
 * UseCase: получить задачи на конкретный день
 *
 * Отвечает за:
 * - получение всех задач из репозитория
 * - фильтрацию по дате
 * - сортировку для отображения в UI
 */
class GetTasksForDay(
    private val repository: TaskRepository
) {


    //  Возвращает список задач, отфильтрованных по дате
    suspend operator fun invoke(
        date: LocalDate
    ): List<Task> {
        // Получаем все задачи из репозитория
        val tasks = repository.getAllTasks()
        return tasks
            // Оставляем только задачи, актуальные на выбранную дату
            .filter { it.isScheduledFor(date) }
            // Сортировка:
            // 1. сначала задачи без времени
            // 2. затем задачи по времени
            .sortedWith(
                compareBy<Task> { it.time == null }
                    .thenBy { it.time }
            )
    }
}







