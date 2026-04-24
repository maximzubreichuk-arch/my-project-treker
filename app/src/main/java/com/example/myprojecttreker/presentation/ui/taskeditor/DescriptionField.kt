package com.example.myprojecttreker.presentation.ui.taskeditor


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Поле ввода описания задачи.
 *
 * Отображает текущее описание и уведомляет об изменениях через onChange.
 *
 * Особенности:
 * - однострочное поле
 * - фиксированная высота
 */
@Composable
fun DescriptionField(
    state: TaskEditorState,
    onChange: (String) -> Unit,

    ) {
    OutlinedTextField(
        value = state.description,
        // Передаём новое значение наверх (в state)
        onValueChange = onChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        label = { Text("Описание") },
        singleLine = true
    )
}