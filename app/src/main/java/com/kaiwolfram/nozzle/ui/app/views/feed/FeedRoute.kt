package com.kaiwolfram.nozzle.ui.app.views.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kaiwolfram.nozzle.model.PostWithMeta

@Composable
fun FeedRoute(
    feedViewModel: FeedViewModel,
    onPrepareReply: (PostWithMeta) -> Unit,
    onPreparePost: (List<String>) -> Unit,
    onOpenDrawer: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToThread: (String, String?, String?) -> Unit,
    onNavigateToReply: () -> Unit,
    onNavigateToPost: () -> Unit,
) {
    val uiState by feedViewModel.uiState.collectAsState()
    val metadataState by feedViewModel.metadataState.collectAsState()

    FeedScreen(
        uiState = uiState,
        metadataState = metadataState,
        onLike = feedViewModel.onLike,
        onRepost = feedViewModel.onRepost,
        onPrepareReply = onPrepareReply,
        onPreparePost = onPreparePost,
        onRefreshFeedView = feedViewModel.onRefreshFeedView,
        onLoadMore = feedViewModel.onLoadMore,
        onPreviousHeadline = feedViewModel.onPreviousHeadline,
        onNextHeadline = feedViewModel.onNextHeadline,
        onOpenDrawer = onOpenDrawer,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToThread = { postIds ->
            onNavigateToThread(
                postIds.id,
                postIds.replyToId,
                postIds.replyToRootId
            )
        },
        onNavigateToReply = onNavigateToReply,
        onNavigateToPost = onNavigateToPost,
    )
}
