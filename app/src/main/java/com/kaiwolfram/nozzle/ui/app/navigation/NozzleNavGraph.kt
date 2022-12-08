package com.kaiwolfram.nozzle.ui.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaiwolfram.nozzle.ui.app.VMContainer
import com.kaiwolfram.nozzle.ui.app.views.chat.ChatRoute
import com.kaiwolfram.nozzle.ui.app.views.feed.FeedRoute
import com.kaiwolfram.nozzle.ui.app.views.keys.KeysRoute
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileRoute
import com.kaiwolfram.nozzle.ui.app.views.relays.RelaysRoute
import com.kaiwolfram.nozzle.ui.app.views.settings.SettingsRoute

@Composable
fun NozzleNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NozzleRoute.FEED,
    vmContainer: VMContainer,
    navActions: NozzleNavActions,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NozzleRoute.PERSONAL_PROFILE) {
            ProfileRoute(
                profileViewModel = vmContainer.profileViewModel,
            )
        }
        composable(NozzleRoute.FEED) {
            FeedRoute(
                feedViewModel = vmContainer.feedViewModel,
            )
        }
        composable(NozzleRoute.CHAT) {
            ChatRoute(
                chatViewModel = vmContainer.chatViewModel,
            )
        }
        composable(NozzleRoute.KEYS) {
            KeysRoute(
                keysViewModel = vmContainer.keysViewModel,
                onUpdateDrawerPubkey = vmContainer.drawerViewModel.onUpdatePubkey,
                onNavigateToFeed = navActions.navigateToFeed,
            )
        }
        composable(NozzleRoute.RELAYS) {
            RelaysRoute(
                relaysViewModel = vmContainer.relaysViewModel,
            )
        }
        composable(NozzleRoute.SETTINGS) {
            SettingsRoute(
                settingsViewModel = vmContainer.settingsViewModel,
                onUpdateDrawerName = vmContainer.drawerViewModel.onUpdateName,
                onNavigateToFeed = navActions.navigateToFeed,
            )
        }
    }
}
