package com.kaiwolfram.nozzle.ui.app.views.profile.followers

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModelState

@Composable
fun FollowersScreen(
    uiState: ProfileViewModelState,
) {
    Text(text = "Followers are coming soon! ${uiState.name}")
}
