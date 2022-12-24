package com.kaiwolfram.nozzle.ui.app.views.editProfile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun EditProfileRoute(
    editProfileViewModel: EditProfileViewModel,
    onResetDrawerUiState: () -> Unit,
    onResetFeedIconUiState: () -> Unit,
    onGoBack: () -> Unit,
) {
    val uiState by editProfileViewModel.uiState.collectAsState()

    EditProfileScreen(
        uiState = uiState,
        onUpdateProfileAndShowToast = { toast ->
            editProfileViewModel.onUpdateProfileAndShowToast(toast)
            onResetDrawerUiState()
            onResetFeedIconUiState()
        },
        onChangeName = editProfileViewModel.onChangeName,
        onChangeBio = editProfileViewModel.onChangeBio,
        onChangePictureUrl = editProfileViewModel.onChangePictureUrl,
        onResetUiState = editProfileViewModel.onResetUiState,
        onGoBack = onGoBack,
    )
}
