package com.kaiwolfram.nozzle.ui.app.relays

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun RelaysScreen(
    uiState: RelaysViewModelState,
) {
    Text(text = uiState.label)
}
