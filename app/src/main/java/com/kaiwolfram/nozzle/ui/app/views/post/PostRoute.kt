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
    val metadataState by postViewModel.metadataState.collectAsState()

    PostScreen(
        uiState = uiState,
        metadataState = metadataState,
        onChangeContent = postViewModel.onChangeContent,
        onSendOrShowErrorToast = postViewModel.onSendOrShowErrorToast,
        onGoBack = onGoBack,
    )
}
