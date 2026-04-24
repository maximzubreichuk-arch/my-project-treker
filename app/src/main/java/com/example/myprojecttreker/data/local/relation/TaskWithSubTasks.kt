package com.example.myprojecttreker.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.myprojecttreker.data.local.entity.SubTaskEntity
import com.example.myprojecttreker.data.local.entity.TaskEntity

/**
 * Связь "задача + подзадачи" для Room.
 *
 * Используется для получения задачи вместе с её подзадачами
 * в одном запросе (через @Transaction).
 *
 * Room автоматически:
 * - загружает TaskEntity
 * - подтягивает все связанные SubTaskEntity по taskId
 */
data class TaskWithSubTasks(
    @Embedded
    // основная задача
    val task: TaskEntity,
    @Relation(
        // поле в TaskEntity
        parentColumn = "id",
        // поле в SubTaskEntity
        entityColumn = "taskId"
    )
    // список подзадач
    val subtasks: List<SubTaskEntity>
)