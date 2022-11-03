package com.kaiwolfram.nozzle.ui.app.profile

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ProfileScreen(
    uiState: ProfileViewModelState,
) {
    Text(text = uiState.label)
}
