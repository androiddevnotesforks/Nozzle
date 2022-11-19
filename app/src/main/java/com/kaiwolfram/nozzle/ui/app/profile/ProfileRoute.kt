package com.kaiwolfram.nozzle.ui.app.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel,
) {
    val uiState by profileViewModel.uiState.collectAsState()

    ProfileRoute(
        uiState = uiState,
        onChangeProfilePictureUrl = profileViewModel.onChangeProfilePictureUrl,
        onChangeName = profileViewModel.onChangeName,
        onChangeBio = profileViewModel.onChangeBio,
        onChangePrivateKey = profileViewModel.onChangePrivateKey,
    )
}

@Composable
private fun ProfileRoute(
    uiState: ProfileViewModelState,
    onChangeProfilePictureUrl: (String) -> Unit,
    onChangeName: (String) -> Unit,
    onChangeBio: (String) -> Unit,
    onChangePrivateKey: (String) -> Unit,
) {
    ProfileScreen(
        uiState = uiState,
        onChangeProfilePictureUrl = onChangeProfilePictureUrl,
        onChangeName = onChangeName,
        onChangeBio = onChangeBio,
        onChangePrivateKey = onChangePrivateKey,
    )
}
