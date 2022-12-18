package com.kaiwolfram.nozzle.ui.app.views.thread

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ThreadRoute(
    threadViewModel: ThreadViewModel,
) {
    val uiState by threadViewModel.uiState.collectAsState()

    ThreadScreen(
        uiState = uiState,
    )
}
