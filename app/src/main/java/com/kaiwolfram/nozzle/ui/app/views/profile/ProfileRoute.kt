package com.kaiwolfram.nozzle.ui.app.views.profile

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kaiwolfram.nozzle.model.PostIds
import com.kaiwolfram.nozzle.model.PostWithMeta

private const val TAG = "ProfileRoute"

@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel,
    onPrepareReply: (PostWithMeta) -> Unit,
    onNavigateToThread: (PostIds) -> Unit,
    onNavigateToReply: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
) {
    val isRefreshing by profileViewModel.isRefreshingState.collectAsState()
    val profile by profileViewModel.profileState.collectAsState()
    val posts by profileViewModel.postsState.collectAsState()
    val forceRecomposition by profileViewModel.forceRecompositionState.collectAsState()
    Log.d(TAG, "Recompose $forceRecomposition")

    ProfileScreen(
        isRefreshing = isRefreshing,
        profile = profile,
        posts = posts,
        onPrepareReply = onPrepareReply,
        onLike = profileViewModel.onLike,
        onRepost = profileViewModel.onRepost,
        onFollow = profileViewModel.onFollow,
        onUnfollow = profileViewModel.onUnfollow,
        onRefreshProfileView = profileViewModel.onRefreshProfileView,
        onCopyNpub = profileViewModel.onCopyNpub,
        onLoadMore = profileViewModel.onLoadMore,
        onNavigateToThread = onNavigateToThread,
        onNavigateToReply = onNavigateToReply,
        onNavigateToEditProfile = onNavigateToEditProfile,
    )
}
