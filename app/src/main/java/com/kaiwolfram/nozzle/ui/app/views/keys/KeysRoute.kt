package com.kaiwolfram.nozzle.ui.app.views.keys

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun KeysRoute(
    keysViewModel: KeysViewModel,
    onNavigateToFeed: () -> Unit,
) {
    val uiState by keysViewModel.uiState.collectAsState()

    KeysScreen(
        uiState = uiState,
        onCopyPubkeyAndShowToast = keysViewModel.onCopyPubkeyAndShowToast,
        onUpdateKeyPairAndShowToast = keysViewModel.onUpdateKeyPairAndShowToast,
        onPrivkeyChange = keysViewModel.onPrivkeyChange,
        onResetUiState = keysViewModel.onResetUiState,
        onNavigateToFeed = onNavigateToFeed,
    )
}
