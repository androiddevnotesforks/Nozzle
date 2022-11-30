package com.kaiwolfram.nozzle.ui.app.views.followers

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun FollowersScreen(
    uiState: FollowersViewModelState,
) {
    Text(text = "Followers are coming soon! ${uiState.lol}")
}
