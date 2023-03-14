package com.kaiwolfram.nozzle.ui.app.views.feed

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.model.RelaySelection

private const val TAG = "FeedRoute"

@Composable
fun FeedRoute(
    feedViewModel: FeedViewModel,
    // TODO: Nav with args, no need for onPrepare
    onPrepareReply: (PostWithMeta) -> Unit,
    onPreparePost: (RelaySelection) -> Unit,
    onOpenDrawer: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToThread: (String, String?, String?) -> Unit,
    onNavigateToReply: () -> Unit,
    onNavigateToPost: () -> Unit,
) {
    val uiState by feedViewModel.uiState.collectAsState()
    val metadataState by feedViewModel.metadataState.collectAsState()
    val feedState by feedViewModel.feedState.collectAsState()
    val forceRecomposition by feedViewModel.forceRecompositionState.collectAsState()
    Log.d(TAG, "Recompose $forceRecomposition")

    FeedScreen(
        uiState = uiState,
        feedState = feedState,
        metadataState = metadataState,
        onLike = feedViewModel.onLike,
        onRepost = feedViewModel.onRepost,
        onPrepareReply = onPrepareReply,
        onPreparePost = onPreparePost,
        onToggleContactsOnly = feedViewModel.onToggleContactsOnly,
        onTogglePosts = feedViewModel.onTogglePosts,
        onToggleReplies = feedViewModel.onToggleReplies,
        onToggleRelayIndex = feedViewModel.onToggleRelayIndex,
        onToggleAutopilot = feedViewModel.onToggleAutopilot,
        onRefreshFeedView = feedViewModel.onRefreshFeedView,
        onRefreshOnMenuDismiss = feedViewModel.onRefreshOnMenuDismiss,
        onLoadMore = feedViewModel.onLoadMore,
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
