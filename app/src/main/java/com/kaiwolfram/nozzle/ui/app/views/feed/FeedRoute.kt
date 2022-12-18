package com.kaiwolfram.nozzle.ui.app.views.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun FeedRoute(
    feedViewModel: FeedViewModel,
    onOpenDrawer: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onNavigateToThread: () -> Unit,
) {
    val uiState by feedViewModel.uiState.collectAsState()

    FeedScreen(
        uiState = uiState,
        onRefreshFeedView = feedViewModel.onRefreshFeedView,
        onOpenDrawer = onOpenDrawer,
        onOpenProfile = onOpenProfile,
        onNavigateToThread = onNavigateToThread
    )
}
