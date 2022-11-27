package com.kaiwolfram.nozzle.ui.app.views.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun FeedRoute(
    feedViewModel: FeedViewModel,
) {
    val uiState by feedViewModel.uiState.collectAsState()

    FeedRoute(
        uiState = uiState,
    )
}

@Composable
private fun FeedRoute(
    uiState: FeedViewModelState,
) {
    FeedScreen(
        uiState = uiState,
    )
}
