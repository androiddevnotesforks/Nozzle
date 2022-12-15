package com.kaiwolfram.nozzle.ui.app.views.relays

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun RelaysRoute(
    relaysViewModel: RelaysViewModel,
    onNavigateToFeed: () -> Unit,
    ) {
    val uiState by relaysViewModel.uiState.collectAsState()

    RelaysScreen(
        uiState = uiState,
        onRemoveRelay = relaysViewModel.onRemoveRelay,
        onAddRelay = relaysViewModel.onAddRelay,
        onNavigateToFeed = onNavigateToFeed,
    )
}
