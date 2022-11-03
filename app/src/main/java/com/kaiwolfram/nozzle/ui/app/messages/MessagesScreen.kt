package com.kaiwolfram.nozzle.ui.app.messages

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun MessagesScreen(
    uiState: MessagesViewModelState,
) {
    Text(text = uiState.label)
}
