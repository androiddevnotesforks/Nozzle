package com.kaiwolfram.nozzle.ui.app.views.profile.followers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModelState

@Composable
fun FollowersRoute(
    profileViewModel: ProfileViewModel,
) {
    val uiState by profileViewModel.uiState.collectAsState()

    FollowersScreen(
        uiState = uiState,
    )
}
