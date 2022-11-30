package com.kaiwolfram.nozzle.ui.app.views.profile.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun EditProfileRoute(
    editProfileViewModel: EditProfileViewModel,
) {
    val uiState by editProfileViewModel.uiState.collectAsState()

    EditProfileScreen(
        uiState = uiState,
    )
}
