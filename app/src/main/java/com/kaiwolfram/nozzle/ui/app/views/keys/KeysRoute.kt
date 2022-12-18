package com.kaiwolfram.nozzle.ui.app.views.keys

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun KeysRoute(
    keysViewModel: KeysViewModel,
    onUpdateDrawerPubkey: () -> Unit,
    onGoBack: () -> Unit,
) {
    val uiState by keysViewModel.uiState.collectAsState()

    KeysScreen(
        uiState = uiState,
        onCopyPubkeyAndShowToast = keysViewModel.onCopyPubkeyAndShowToast,
        onCopyPrivkeyAndShowToast = keysViewModel.onCopyPrivkeyAndShowToast,
        onUpdateKeyPairAndShowToast = keysViewModel.onUpdateKeyPairAndShowToast,
        onChangePrivkey = keysViewModel.onChangePrivkey,
        onUpdateDrawerPubkey = onUpdateDrawerPubkey,
        onResetUiState = keysViewModel.onResetUiState,
        onGoBack = onGoBack,
    )
}
