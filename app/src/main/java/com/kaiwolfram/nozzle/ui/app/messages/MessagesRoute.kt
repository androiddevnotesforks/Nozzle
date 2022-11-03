package com.kaiwolfram.nozzle.ui.app.messages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun MessagesRoute(
    messagesViewModel: MessagesViewModel,
) {
    val uiState by messagesViewModel.uiState.collectAsState()

    MessagesRoute(
        uiState = uiState,
    )
}

@Composable
private fun MessagesRoute(
    uiState: MessagesViewModelState,
) {
    MessagesScreen(
        uiState = uiState,
    )
}
