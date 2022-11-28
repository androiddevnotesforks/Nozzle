package com.kaiwolfram.nozzle.ui.app.views.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel,
    navToFollowing: () -> Unit,
    navToFollowers: () -> Unit,
    navToEditProfile: () -> Unit,
) {
    val uiState by profileViewModel.uiState.collectAsState()

    ProfileScreen(
        uiState = uiState,
        navToFollowing = navToFollowing,
        navToFollowers = navToFollowers,
        navToEditProfile = navToEditProfile,
        onGetPicture = profileViewModel.onGetPicture,
    )
}
