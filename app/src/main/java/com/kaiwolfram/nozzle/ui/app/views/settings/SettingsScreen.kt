package com.kaiwolfram.nozzle.ui.app.views.settings

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun SettingsScreen(
    uiState: SettingsViewModelState,
) {
    Text(text = uiState.label)
}
