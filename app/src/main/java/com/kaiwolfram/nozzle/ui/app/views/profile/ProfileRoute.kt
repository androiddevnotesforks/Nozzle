package com.kaiwolfram.nozzle.ui.app.views.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel,
) {
    ProfileRoute(
        profileViewModel = profileViewModel,
        navToEditProfile = null
    )
}


@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel,
    navToEditProfile: (() -> Unit)?,
) {
    val uiState by profileViewModel.uiState.collectAsState()

    ProfileScreen(
        uiState = uiState,
        navToEditProfile = navToEditProfile,
        onGetPicture = profileViewModel.onGetPicture,
        onRefreshProfileView = profileViewModel.onRefreshProfileView,
    )
}
