package com.kaiwolfram.nozzle.ui.app.navigation

import androidx.compose.material.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaiwolfram.nozzle.ui.app.VMContainer
import com.kaiwolfram.nozzle.ui.app.views.feed.FeedRoute
import com.kaiwolfram.nozzle.ui.app.views.keys.KeysRoute
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileRoute
import com.kaiwolfram.nozzle.ui.app.views.settings.SettingsRoute
import com.kaiwolfram.nozzle.ui.app.views.thread.ThreadRoute
import kotlinx.coroutines.launch

@Composable
fun NozzleNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NozzleRoute.FEED,
    vmContainer: VMContainer,
    navActions: NozzleNavActions,
    drawerState: DrawerState,
) {
    val scope = rememberCoroutineScope()
    val onNavigateToProfile = remember {
        { pubkey: String ->
            run {
                vmContainer.profileViewModel.onSetPubkey(pubkey)
                navActions.navigateToProfile()
            }
        }
    }
    val onNavigateToThread = remember {
        { postId: String ->
            run {
                vmContainer.threadViewModel.onOpenThread(postId)
                navActions.navigateToThread()
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NozzleRoute.FEED) {
            FeedRoute(
                feedViewModel = vmContainer.feedViewModel,
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToThread = onNavigateToThread,
            )
        }
        composable(NozzleRoute.PROFILE) {
            ProfileRoute(
                profileViewModel = vmContainer.profileViewModel,
                onNavigateToThread = onNavigateToThread,
            )
        }
        composable(NozzleRoute.KEYS) {
            KeysRoute(
                keysViewModel = vmContainer.keysViewModel,
                onResetDrawerUiState = vmContainer.drawerViewModel.onResetUiState,
                onResetFeedIconUiState = vmContainer.feedViewModel.onResetProfileIconUiState,
                onResetSettingsUiState = vmContainer.settingsViewModel.onResetUiState,
                onGoBack = navActions.popStack,
            )
        }
        composable(NozzleRoute.SETTINGS) {
            SettingsRoute(
                settingsViewModel = vmContainer.settingsViewModel,
                onResetDrawerUiState = vmContainer.drawerViewModel.onResetUiState,
                onResetFeedIconUiState = vmContainer.feedViewModel.onResetProfileIconUiState,
                onGoBack = navActions.popStack,
            )
        }
        composable(NozzleRoute.THREAD) {
            ThreadRoute(
                threadViewModel = vmContainer.threadViewModel,
                onNavigateToProfile = onNavigateToProfile,
                onGoBack = navActions.popStack,
            )
        }
    }
}
