package com.kaiwolfram.nozzle.ui.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaiwolfram.nozzle.ui.app.VMContainer
import com.kaiwolfram.nozzle.ui.app.feed.FeedRoute
import com.kaiwolfram.nozzle.ui.app.messages.MessagesRoute
import com.kaiwolfram.nozzle.ui.app.profile.ProfileRoute
import com.kaiwolfram.nozzle.ui.app.search.SearchRoute

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
        composable(NozzleRoute.SEARCH) {
            SearchRoute(
                searchViewModel = vmContainer.searchViewModel,
            )
        }
        composable(NozzleRoute.MESSAGES) {
            MessagesRoute(
                messagesViewModel = vmContainer.messagesViewModel,
            )
        }
    }
}
