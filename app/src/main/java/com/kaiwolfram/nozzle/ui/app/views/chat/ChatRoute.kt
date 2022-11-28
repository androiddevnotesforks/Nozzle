package com.kaiwolfram.nozzle.ui.app.views.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ChatRoute(
    chatViewModel: ChatViewModel,
) {
    val uiState by chatViewModel.uiState.collectAsState()

    ChatScreen(
        uiState = uiState,
    )
}
