package com.kaiwolfram.nozzle.ui.app.views.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel,
    onResetDrawerUiState: () -> Unit,
    onResetFeedIconUiState: () -> Unit,
    onGoBack: () -> Unit,
) {
    val uiState by settingsViewModel.uiState.collectAsState()

    SettingsScreen(
        uiState = uiState,
        onUpdateProfileAndShowToast = { toast ->
            settingsViewModel.onUpdateProfileAndShowToast(toast)
            onResetDrawerUiState()
            onResetFeedIconUiState()
        },
        onChangeName = settingsViewModel.onChangeName,
        onChangeBio = settingsViewModel.onChangeBio,
        onChangePictureUrl = settingsViewModel.onChangePictureUrl,
        onResetUiState = settingsViewModel.onResetUiState,
        onGoBack = onGoBack,
    )
}
