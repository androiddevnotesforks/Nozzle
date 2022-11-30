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
import com.kaiwolfram.nozzle.ui.app.views.profile.edit.EditProfileRoute
import com.kaiwolfram.nozzle.ui.app.views.profile.followers.FollowersRoute
import com.kaiwolfram.nozzle.ui.app.views.profile.following.FollowingRoute
import com.kaiwolfram.nozzle.ui.app.views.relays.RelaysRoute

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
        composable(NozzleRoute.PROFILE) {
            ProfileRoute(
                profileViewModel = vmContainer.profileViewModel,
                navToFollowing = navActions.navigateToFollowing,
                navToFollowers = navActions.navigateToFollowers,
                navToEditProfile = navActions.navigateToEditProfile,
            )
        }
        composable(NozzleRoute.EDIT_PROFILE) {
            EditProfileRoute(
                profileViewModel = vmContainer.profileViewModel,
            )
        }
        composable(NozzleRoute.FOLLOWING) {
            FollowingRoute(
                profileViewModel = vmContainer.profileViewModel,
            )
        }
        composable(NozzleRoute.FOLLOWERS) {
            FollowersRoute(
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
    }
}
