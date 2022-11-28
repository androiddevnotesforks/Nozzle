package com.kaiwolfram.nozzle.ui.app.views.feed

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun FeedScreen(
    uiState: FeedViewModelState,
) {
    Text(text = uiState.label)
}
