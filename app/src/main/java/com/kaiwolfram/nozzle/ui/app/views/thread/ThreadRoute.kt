package com.kaiwolfram.nozzle.ui.app.views.thread

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ThreadRoute(
    threadViewModel: ThreadViewModel,
    onNavigateToProfile: (String) -> Unit,
    onGoBack: () -> Unit,
) {
    val uiState by threadViewModel.uiState.collectAsState()

    ThreadScreen(
        uiState = uiState,
        onRefreshThreadView = threadViewModel.onRefreshThreadView,
        onLike = threadViewModel.onLike,
        onRepost = threadViewModel.onRepost,
        onOpenThread = threadViewModel.onOpenThread,
        onNavigateToProfile = onNavigateToProfile,
        onGoBack = onGoBack,
    )
}
