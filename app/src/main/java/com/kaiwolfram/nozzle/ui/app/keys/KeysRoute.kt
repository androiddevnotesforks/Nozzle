package com.kaiwolfram.nozzle.ui.app.keys

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun KeysRoute(
    keysViewModel: KeysViewModel,
) {
    val uiState by keysViewModel.uiState.collectAsState()

    KeysRoute(
        uiState = uiState,
    )
}

@Composable
private fun KeysRoute(
    uiState: KeysViewModelState,
) {
    KeysScreen(
        uiState = uiState,
    )
}
