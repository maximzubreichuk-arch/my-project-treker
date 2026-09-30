package com.example.myprojecttreker.presentation.ui.main.taskeditor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myprojecttreker.R

/**
 * Кнопка сохранения, закреплённая в нижней панели редактора.
 * Всегда остаётся доступной и поднимается над экранной клавиатурой/навигацией.
 */
@Composable
fun SaveButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(stringResource(R.string.save))
    }
}
