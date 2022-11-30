package com.kaiwolfram.nozzle.ui.app.views.profile.edit

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModelState

@Composable
fun EditProfileScreen(
    uiState: EditProfileViewModelState,
) {
    Text(text = "Edit is coming soon! ${uiState.lol}")
}
