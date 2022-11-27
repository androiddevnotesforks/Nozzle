package com.kaiwolfram.nozzle.ui.app.support

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun SupportScreen(
    uiState: SupportViewModelState,
) {
    Text(text = uiState.label)
}
