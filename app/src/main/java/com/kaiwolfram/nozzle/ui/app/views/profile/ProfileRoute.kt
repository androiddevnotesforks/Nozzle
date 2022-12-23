package com.kaiwolfram.nozzle.ui.app.views.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel,
    onNavigateToThread: (String) -> Unit,
    onNavigateToReply: () -> Unit,
) {
    val uiState by profileViewModel.uiState.collectAsState()

    ProfileScreen(
        uiState = uiState,
        onLike = profileViewModel.onLike,
        onRepost = profileViewModel.onRepost,
        onFollow = profileViewModel.onFollow,
        onUnfollow = profileViewModel.onUnfollow,
        onRefreshProfileView = profileViewModel.onRefreshProfileView,
        onCopyPubkeyAndShowToast = profileViewModel.onCopyPubkeyAndShowToast,
        onNavigateToThread = onNavigateToThread,
        onNavigateToReply = onNavigateToReply,
    )
}
