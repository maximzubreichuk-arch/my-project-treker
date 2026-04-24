package com.example.myprojecttreker.presentation.ui.taskeditor


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Поле ввода названия задачи.
 *
 * Отвечает за:
 * - отображение текущего названия из состояния
 * - передачу введённого текста наружу через onChange
 *
 * Особенности:
 * - однострочное поле (singleLine)
 * - не содержит собственной логики — полностью управляется внешним state
 */
@Composable
fun TitleField(
    state: TaskEditorState,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        // Текущее значение названия задачи
        value = state.title,
        // Передаём введённый текст наружу (state hoisting)
        onValueChange = onChange,
        modifier = Modifier
            .fillMaxWidth()
            // Фиксированная высота для единообразия UI
            .height(55.dp),
        label = { Text("Название") },
        // Ограничение ввода одной строкой
        singleLine = true
    )
}