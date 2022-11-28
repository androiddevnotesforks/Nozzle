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

    ProfileRoute(
        uiState = uiState,
        navToFollowing = navToFollowing,
        navToFollowers = navToFollowers,
        navToEditProfile = navToEditProfile,
    )
}

@Composable
private fun ProfileRoute(
    uiState: ProfileViewModelState,
    navToFollowing: () -> Unit,
    navToFollowers: () -> Unit,
    navToEditProfile: () -> Unit,
) {
    ProfileScreen(
        uiState = uiState,
        navToFollowing = navToFollowing,
        navToFollowers = navToFollowers,
        navToEditProfile = navToEditProfile,
    )
}
