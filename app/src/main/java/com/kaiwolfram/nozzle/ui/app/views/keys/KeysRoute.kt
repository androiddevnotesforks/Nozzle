package com.kaiwolfram.nozzle.ui.app.views.keys

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun KeysRoute(
    keysViewModel: KeysViewModel,
    onResetDrawerUiState: () -> Unit,
    onResetFeedIconUiState: () -> Unit,
    onResetEditProfileUiState: () -> Unit,
    onGoBack: () -> Unit,
) {
    val uiState by keysViewModel.uiState.collectAsState()

    KeysScreen(
        uiState = uiState,
        onCopyNpubAndShowToast = keysViewModel.onCopyNpubAndShowToast,
        onCopyPrivkeyAndShowToast = keysViewModel.onCopyPrivkeyAndShowToast,
        onUpdateKeyPairAndShowToast = { toast ->
            keysViewModel.onUpdateKeyPairAndShowToast(toast)
            onResetDrawerUiState()
            onResetFeedIconUiState()
            onResetEditProfileUiState()
        },
        onChangePrivkey = keysViewModel.onChangePrivkey,
        onResetUiState = keysViewModel.onResetUiState,
        onCanGoBack = keysViewModel.onCanGoBack,
        onGoBack = onGoBack,
    )
}
