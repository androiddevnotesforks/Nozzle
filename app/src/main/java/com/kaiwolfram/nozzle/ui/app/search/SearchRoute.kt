package com.kaiwolfram.nozzle.ui.app.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SearchRoute(
    searchViewModel: SearchViewModel,
) {
    val uiState by searchViewModel.uiState.collectAsState()

    SearchRoute(
        uiState = uiState,
    )
}

@Composable
private fun SearchRoute(
    uiState: SearchViewModelState,
) {
    SearchScreen(
        uiState = uiState,
    )
}
