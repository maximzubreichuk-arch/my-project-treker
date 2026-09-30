package com.example.myprojecttreker.presentation.ui.main.taskeditor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myprojecttreker.R
import com.example.myprojecttreker.domain.SubTask

/**
 * Подзадачи скрыты по умолчанию и раскрываются как компактный выпадающий блок.
 * Внутреннего LazyColumn нет: внешний редактор управляет единственным скроллом.
 */
@Composable
fun SubtasksEditor(
    state: TaskEditorState,
    onUpdate: (TaskEditorState) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.subtasks)} (${state.subtasks.size})",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            label = { Text(stringResource(R.string.subtask)) }
                        )
                        IconButton(
                            onClick = {
                                val title = text.trim()
                                if (title.isNotEmpty()) {
                                    onUpdate(
                                        state.copy(
                                            subtasks = state.subtasks + SubTask(
                                                id = System.currentTimeMillis(),
                                                title = title,
                                                isDone = false
                                            )
                                        )
                                    )
                                    text = ""
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_subtask)
                            )
                        }
                    }

                    state.subtasks.forEach { subtask ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = subtask.isDone,
                                onCheckedChange = { checked ->
                                    onUpdate(
                                        state.copy(
                                            subtasks = state.subtasks.map {
                                                if (it.id == subtask.id) it.copy(isDone = checked) else it
                                            }
                                        )
                                    )
                                }
                            )
                            Text(
                                text = subtask.title,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            IconButton(
                                onClick = {
                                    onUpdate(
                                        state.copy(
                                            subtasks = state.subtasks.filterNot { it.id == subtask.id }
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
