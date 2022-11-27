package com.kaiwolfram.nozzle.ui.app.keys

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun KeysScreen(
    uiState: KeysViewModelState,
) {
    Text(text = uiState.label)
}
