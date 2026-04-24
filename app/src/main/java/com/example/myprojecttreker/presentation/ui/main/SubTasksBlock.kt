package com.example.myprojecttreker.presentation.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myprojecttreker.domain.model.Task

/**
 * Блок отображения и управления подзадачами внутри задачи.
 *
 * Отвечает за:
 * - отображение списка подзадач
 * - изменение состояния выполнения подзадач
 * - синхронизацию состояния всей задачи (isDone)
 *
 * Особенности:
 * - работает с immutable списком (обновление через copy)
 * - автоматически обновляет статус всей задачи, если все подзадачи выполнены
 * - не хранит состояние — полностью управляется извне
 */
@Composable
fun SubTasksBlock(
    task: Task,
    onUpdate: (Task) -> Unit
) {
    Column(
        // Отступ слева для визуальной иерархии (вложенность подзадач)
        modifier = Modifier.padding(start = 48.dp)
    ) {
        // Проходим по всем подзадачам
        task.subtasks.forEach { sub ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    // Текущее состояние подзадачи
                    checked = sub.isDone,
                    onCheckedChange = { checked ->

                        // Обновляем список подзадач (immutable подход)
                        val updatedSubtasks = task.subtasks.map {

                            // Меняем только выбранную подзадачу
                            if (it.id == sub.id)
                                it.copy(isDone = checked)
                            else it
                        }
                        // Проверяем: выполнены ли ВСЕ подзадачи
                        val allDone = updatedSubtasks.all { it.isDone }

                        // Обновляем всю задачу:
                        // - список подзадач
                        // - общий статус выполнения
                        onUpdate(
                            task.copy(
                                subtasks = updatedSubtasks,
                                isDone = allDone
                            )
                        )
                    }
                )

                Text(
                    // Название подзадачи
                    text = sub.title,
                    // Занимает всё доступное пространство
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
