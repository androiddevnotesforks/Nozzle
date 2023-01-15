package com.kaiwolfram.nozzle.ui.app.views.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SearchRoute(
    searchViewModel: SearchViewModel,
    onNavigateToProfile: (String) -> Unit,
    onGoBack: () -> Unit,
) {
    val uiState by searchViewModel.uiState.collectAsState()

    SearchScreen(
        uiState = uiState,
        onChangeInput = searchViewModel.onChangeInput,
        onValidateAndNavigateToProfile = {
            searchViewModel.onValidateAndNavigateToProfile(onNavigateToProfile)
        },
        onResetUI = searchViewModel.onResetUI,
        onGoBack = onGoBack,
    )
}
