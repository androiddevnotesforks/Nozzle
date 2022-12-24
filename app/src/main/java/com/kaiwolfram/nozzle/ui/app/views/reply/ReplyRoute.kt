package com.kaiwolfram.nozzle.ui.app.views.reply

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ReplyRoute(
    replyViewModel: ReplyViewModel,
    onGoBack: () -> Unit,
) {
    val uiState by replyViewModel.uiState.collectAsState()

    ReplyScreen(
        uiState = uiState,
        onChangeReply = replyViewModel.onChangeReply,
        onSend = replyViewModel.onSend,
        onGoBack = onGoBack,
    )
}
