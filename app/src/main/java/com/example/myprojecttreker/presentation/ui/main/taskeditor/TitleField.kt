package com.example.myprojecttreker.presentation.ui.main.taskeditor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.myprojecttreker.R

@Composable
fun TitleField(
    state: TaskEditorState,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = state.title,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.title)) },
        singleLine = true
    )
}
