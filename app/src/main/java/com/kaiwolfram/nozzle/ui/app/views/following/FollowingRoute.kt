package com.kaiwolfram.nozzle.ui.app.views.following

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun FollowingRoute(
    followingViewModel: FollowingViewModel,
) {
    val uiState by followingViewModel.uiState.collectAsState()

    FollowingScreen(
        uiState = uiState,
    )
}
