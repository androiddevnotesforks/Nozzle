package com.kaiwolfram.nozzle.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kaiwolfram.nozzle.ui.app.NozzleNavActions
import com.kaiwolfram.nozzle.ui.app.NozzleScaffold
import com.kaiwolfram.nozzle.ui.app.VMContainer
import com.kaiwolfram.nozzle.ui.app.feed.FeedViewModel
import com.kaiwolfram.nozzle.ui.app.messages.MessagesViewModel
import com.kaiwolfram.nozzle.ui.app.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.search.SearchViewModel
import com.kaiwolfram.nozzle.ui.theme.NozzleTheme

@Composable
fun NozzleApp() {
    NozzleTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val vmContainer = VMContainer(
                profileViewModel = viewModel(
                    factory = ProfileViewModel.provideFactory()
                ),
                feedViewModel = viewModel(
                    factory = FeedViewModel.provideFactory()
                ),
                searchViewModel = viewModel(
                    factory = SearchViewModel.provideFactory()
                ),
                messagesViewModel = viewModel(
                    factory = MessagesViewModel.provideFactory()
                ),
            )

            val navController = rememberNavController()
            val navActions = remember(navController) {
                NozzleNavActions(navController)
            }

            Row(
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                NozzleScaffold(
                    vmContainer = vmContainer,
                    navActions = navActions,
                    navController = navController
                )
            }
        }
    }
}
