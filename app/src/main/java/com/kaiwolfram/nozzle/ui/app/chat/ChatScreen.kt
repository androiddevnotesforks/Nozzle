package com.kaiwolfram.nozzle.ui.app.chat

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ChatScreen(
    uiState: ChatViewModelState,
) {
    Text(text = uiState.label)
}
