package com.kaiwolfram.nozzle.ui.app.views.profile.following

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModelState

@Composable
fun FollowingScreen(
    uiState: ProfileViewModelState,
) {
    Text(text = "Following is coming soon! ${uiState.name}")
}
