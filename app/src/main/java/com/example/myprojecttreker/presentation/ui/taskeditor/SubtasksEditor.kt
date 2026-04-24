package com.example.myprojecttreker.presentation.ui.taskeditor


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myprojecttreker.domain.model.SubTask

/**
 * Компонент для управления подзадачами внутри редактора задачи.
 *
 * Позволяет:
 * - добавлять новые подзадачи
 * - отмечать выполнение
 * - удалять подзадачи
 *
 * Работает полностью через state → не хранит данные локально,
 * все изменения пробрасываются наружу через onUpdate.
 */
@Composable
fun SubtasksEditor(
    state: TaskEditorState,
    onUpdate: (TaskEditorState) -> Unit
) {
    // текст ввода новой подзадачи
    var text by remember { mutableStateOf("") }
    // состояние скролла списка подзадач
    val scrollState = rememberScrollState()

    // При добавлении новой подзадачи автоматически прокручиваем список вниз,
    // чтобы пользователь сразу видел добавленный элемент.
    LaunchedEffect(state.subtasks.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // поле ввода текста новой подзадачи
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                label = { Text("Подзадача") }
            )
            // кнопка добавления новой подзадачи
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onUpdate(
                            state.copy(
                                // добавляем подзадачу в конец списка (сохраняем порядок)
                                subtasks = state.subtasks + SubTask(
                                    id = System.currentTimeMillis(),
                                    title = text,
                                    isDone = false
                                )
                            )
                        )
                        // очищаем поле после добавления
                        text = ""
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .verticalScroll(scrollState)
                .padding(
                    // добавляем отступ снизу, чтобы список не перекрывался навигационной панелью
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding() + 72.dp
                )
        ) {
            // отображаем список подзадач
            state.subtasks.forEach { subtask ->

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // чекбокс выполнения подзадачи
                    Checkbox(
                        checked = subtask.isDone,
                        onCheckedChange = { checked ->
                            onUpdate(
                                state.copy(
                                    // обновляем состояние только у выбранной подзадачи
                                    subtasks = state.subtasks.map {
                                        if (it.id == subtask.id)
                                            it.copy(isDone = checked)
                                        else it
                                    }
                                )
                            )
                        }
                    )
                    // название подзадачи
                    Text(
                        text = subtask.title,
                        modifier = Modifier.weight(1f)
                    )
                    // кнопка удаления подзадачи
                    IconButton(
                        onClick = {
                            onUpdate(
                                state.copy(
                                    // удаляем подзадачу по id
                                    subtasks = state.subtasks.filter {
                                        it.id != subtask.id
                                    }
                                )
                            )
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                }
            }
        }
    }
}