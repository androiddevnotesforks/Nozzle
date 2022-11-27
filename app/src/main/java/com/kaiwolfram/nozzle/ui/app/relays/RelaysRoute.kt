package com.kaiwolfram.nozzle.ui.app.relays

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun RelaysRoute(
    relaysViewModel: RelaysViewModel,
) {
    val uiState by relaysViewModel.uiState.collectAsState()

    RelaysRoute(
        uiState = uiState,
    )
}

@Composable
private fun RelaysRoute(
    uiState: RelaysViewModelState,
) {
    RelaysScreen(
        uiState = uiState,
    )
}
