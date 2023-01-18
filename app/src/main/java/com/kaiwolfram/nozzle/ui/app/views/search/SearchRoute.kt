package com.kaiwolfram.nozzle.ui.app.views.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kaiwolfram.nozzle.model.PostIds

@Composable
fun SearchRoute(
    searchViewModel: SearchViewModel,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToThread: (PostIds) -> Unit,
    onGoBack: () -> Unit,
) {
    val uiState by searchViewModel.uiState.collectAsState()

    SearchScreen(
        uiState = uiState,
        onChangeInput = searchViewModel.onChangeInput,
        onValidateAndNavigateToDestination = {
            searchViewModel.onValidateAndNavigateToDestination(
                onNavigateToProfile,
                onNavigateToThread
            )
        },
        onResetUI = searchViewModel.onResetUI,
        onGoBack = onGoBack,
    )
}
