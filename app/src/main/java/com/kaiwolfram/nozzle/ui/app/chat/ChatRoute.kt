package com.kaiwolfram.nozzle.ui.app.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ChatRoute(
    chatViewModel: ChatViewModel,
) {
    val uiState by chatViewModel.uiState.collectAsState()

    ChatRoute(
        uiState = uiState,
    )
}

@Composable
private fun ChatRoute(
    uiState: ChatViewModelState,
) {
    ChatScreen(
        uiState = uiState,
    )
}
