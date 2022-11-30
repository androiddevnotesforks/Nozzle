package com.kaiwolfram.nozzle.ui.app.views.followers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun FollowersRoute(
    followersViewModel: FollowersViewModel,
) {
    val uiState by followersViewModel.uiState.collectAsState()

    FollowersScreen(
        uiState = uiState,
    )
}
