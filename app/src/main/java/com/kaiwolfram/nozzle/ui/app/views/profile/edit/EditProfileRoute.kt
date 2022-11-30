package com.kaiwolfram.nozzle.ui.app.views.profile.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModel

@Composable
fun EditProfileRoute(
    profileViewModel: ProfileViewModel,
) {
    val uiState by profileViewModel.uiState.collectAsState()

    EditProfileScreen(
        uiState = uiState,
    )
}
