package com.kaiwolfram.nozzle.ui.app.views.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kaiwolfram.nozzle.model.PostWithMeta

@Composable
fun FeedRoute(
    feedViewModel: FeedViewModel,
    onPrepareReply: (PostWithMeta) -> Unit,
    onPreparePost: () -> Unit,
    onOpenDrawer: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToThread: (String) -> Unit,
    onNavigateToReply: () -> Unit,
    onNavigateToPost: () -> Unit,
) {
    val uiState by feedViewModel.uiState.collectAsState()

    FeedScreen(
        uiState = uiState,
        onLike = feedViewModel.onLike,
        onRepost = feedViewModel.onRepost,
        onPrepareReply = onPrepareReply,
        onPreparePost = onPreparePost,
        onSubscribeToFeed = feedViewModel.onSubscribeToFeed,
        onRefreshFeedView = feedViewModel.onRefreshFeedView,
        onOpenDrawer = onOpenDrawer,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToThread = onNavigateToThread,
        onNavigateToReply = onNavigateToReply,
        onNavigateToPost = onNavigateToPost,
    )
}
