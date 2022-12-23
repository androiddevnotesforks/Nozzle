package com.kaiwolfram.nozzle.ui.app.views.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun FeedRoute(
    feedViewModel: FeedViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToThread: (String) -> Unit,
    onNavigateToReply: () -> Unit,
) {
    val uiState by feedViewModel.uiState.collectAsState()

    FeedScreen(
        uiState = uiState,
        onLike = feedViewModel.onLike,
        onRepost = feedViewModel.onRepost,
        onRefreshFeedView = feedViewModel.onRefreshFeedView,
        onOpenDrawer = onOpenDrawer,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToThread = onNavigateToThread,
        onNavigateToReply = onNavigateToReply,
    )
}
