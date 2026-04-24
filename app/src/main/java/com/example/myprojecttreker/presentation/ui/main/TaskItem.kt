package com.example.myprojecttreker.presentation.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myprojecttreker.domain.model.Task
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import com.example.myprojecttreker.data.local.entity.DayResultEntity
import com.example.myprojecttreker.data.mapper.toSubTasks
import com.example.myprojecttreker.domain.model.SubTask
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


/**
 * UI элемент одной задачи (или её инстанса по времени).
 *
 * Отвечает за:
 * - отображение задачи
 * - чекбокс выполнения
 * - раскрытие подзадач
 * - меню (редактировать / удалить)
 * - синхронизация состояния с dayResults
 */
@Composable
fun TaskItem(
    task: Task,
    time: LocalTime?,
    selectedDate: LocalDate,
    dayResults: List<DayResultEntity>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onDelete: (Task) -> Unit,
    onEdit: (Task) -> Unit,
    onToggleDayResult: (Long, LocalDate, String, Boolean, List<SubTask>) -> Unit
) {

    // Состояние меню (три точки)
    var menuExpanded by remember { mutableStateOf(false) }

    // Состояние диалога удаления
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Ключ времени (используется в БД)
    val timeKey = time.toString()

    //Ищем результат выполнения задачи на конкретный день и время
    val result = dayResults.find {
        it.taskId == task.id &&
                it.date == selectedDate &&
                it.time == timeKey
    }


//      Подзадачи на этот день:
//      - если есть сохранённые - берём их
//      - иначе используем базовые из task

    val daySubtasks = result?.subtasksJson?.toSubTasks()
        ?: task.subtasks

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            //  Состояние чекбокса:
            //  если есть результат - берём его, иначе false

            val isChecked = result?.isDone ?: false
            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    val newValue = !isChecked


                    //  При изменении состояния:
                     // - синхронно обновляем ВСЕ подзадачи

                    val updatedSubtasks = daySubtasks.map {
                        it.copy(isDone = newValue)
                    }

                    // Отправляем изменение в ViewModel
                    onToggleDayResult(
                        task.id,
                        selectedDate,
                        timeKey,
                        newValue,
                        updatedSubtasks
                    )
                }
            )

            Column(modifier = Modifier.weight(1f)) {


                 // Отображение времени (если есть)

                if (time != null) {
                    Text(
                        text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    // нет времени — ничего не показываем

                }
                // Название задачи
                Text(text = task.title)
            }


             // Кнопка раскрытия подзадач
                         IconButton(
                onClick = {
                    onExpandedChange(!expanded)
                }
            ) {
                Icon(
                    if (expanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    null
                )
            }

            // Меню (редактировать / удалить)
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreHoriz, null)
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {

                    DropdownMenuItem(
                        text = { Text("Редактировать") },
                        onClick = {
                            menuExpanded = false
                            onEdit(task)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Удалить") },
                        onClick = {
                            menuExpanded = false
                            showDeleteDialog = true
                        }
                    )
                }

                // Диалог подтверждения удаления
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showDeleteDialog = false
                        },
                        title = { Text("Удалить задачу?") },
                        text = {
                            Text("Вы уверены, что хотите удалить эту задачу?")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDeleteDialog = false
                                    onDelete(task)
                                }
                            ) {
                                Text("Удалить")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDeleteDialog = false
                                }
                            ) {
                                Text("Отмена")
                            }
                        }
                    )
                }
            }
        }

       // Блок подзадач (если раскрыто)
        if (expanded) {
            SubTasksBlock(
                task = task.copy(subtasks = daySubtasks),
                onUpdate = { updatedTask ->

                    // Проверяем: все ли подзадачи выполнены
                    val allDone = updatedTask.subtasks.all { it.isDone }

                    // Сохраняем новое состояние
                    onToggleDayResult(
                        task.id,
                        selectedDate,
                        timeKey,
                        allDone,
                        updatedTask.subtasks
                    )
                }
            )
        }
    }
}