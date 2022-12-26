package com.kaiwolfram.nozzle.ui.app.views.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun PostRoute(
    postViewModel: PostViewModel,
    onGoBack: () -> Unit,
) {
    val uiState by postViewModel.uiState.collectAsState()

    PostScreen(
        uiState = uiState,
        onChangeContent = postViewModel.onChangeContent,
        onSendOrShowErrorToast = postViewModel.onSendOrShowErrorToast,
        onGoBack = onGoBack,
    )
}
