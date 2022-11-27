package com.kaiwolfram.nozzle.ui.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaiwolfram.nozzle.ui.app.VMContainer
import com.kaiwolfram.nozzle.ui.app.chat.ChatRoute
import com.kaiwolfram.nozzle.ui.app.feed.FeedRoute
import com.kaiwolfram.nozzle.ui.app.keys.KeysRoute
import com.kaiwolfram.nozzle.ui.app.profile.ProfileRoute
import com.kaiwolfram.nozzle.ui.app.relays.RelaysRoute
import com.kaiwolfram.nozzle.ui.app.support.SupportRoute

@Composable
fun NozzleNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NozzleRoute.FEED,
    vmContainer: VMContainer,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NozzleRoute.PROFILE) {
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
            )
        }
        composable(NozzleRoute.RELAYS) {
            RelaysRoute(
                relaysViewModel = vmContainer.relaysViewModel,
            )
        }
        composable(NozzleRoute.SUPPORT) {
            SupportRoute(
                supportViewModel = vmContainer.supportViewModel,
            )
        }
    }
}
