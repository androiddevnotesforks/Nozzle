package com.kaiwolfram.nozzle.ui.app.views.support

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SupportRoute(
    supportViewModel: SupportViewModel,
) {
    val uiState by supportViewModel.uiState.collectAsState()

    SupportScreen(
        uiState = uiState,
    )
}
