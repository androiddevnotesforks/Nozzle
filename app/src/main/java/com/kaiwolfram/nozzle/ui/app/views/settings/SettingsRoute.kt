package com.kaiwolfram.nozzle.ui.app.views.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel,
    onUpdateDrawerName: () -> Unit,
    onNavigateToFeed: () -> Unit,
) {
    val uiState by settingsViewModel.uiState.collectAsState()

    SettingsScreen(
        uiState = uiState,
        onUpdateProfileAndShowToast = settingsViewModel.onUpdateProfileAndShowToast,
        onUpdateDrawerName = onUpdateDrawerName,
        onResetUiState = settingsViewModel.onResetUiState,
        onNavigateToFeed = onNavigateToFeed,
    )
}
