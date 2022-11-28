package com.kaiwolfram.nozzle.ui.app.views.profile.following

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModelState

@Composable
fun FollowingRoute(
    profileViewModel: ProfileViewModel,
) {
    val uiState by profileViewModel.uiState.collectAsState()

    FollowingRoute(
        uiState = uiState,
    )
}

@Composable
private fun FollowingRoute(
    uiState: ProfileViewModelState,
) {
    FollowingScreen(
        uiState = uiState,
    )
}
