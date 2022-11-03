package com.kaiwolfram.nozzle.ui.app.search

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun SearchScreen(
    uiState: SearchViewModelState,
) {
    Text(text = uiState.label)
}
