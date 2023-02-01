package com.kaiwolfram.nozzle.ui.app.navigation

import androidx.compose.material.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kaiwolfram.nozzle.model.PostIds
import com.kaiwolfram.nozzle.ui.app.VMContainer
import com.kaiwolfram.nozzle.ui.app.views.editProfile.EditProfileRoute
import com.kaiwolfram.nozzle.ui.app.views.feed.FeedRoute
import com.kaiwolfram.nozzle.ui.app.views.keys.KeysRoute
import com.kaiwolfram.nozzle.ui.app.views.post.PostRoute
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileRoute
import com.kaiwolfram.nozzle.ui.app.views.reply.ReplyRoute
import com.kaiwolfram.nozzle.ui.app.views.search.SearchRoute
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
    val onNavigateToEditProfile = remember {
        {
            run {
                vmContainer.editProfileViewModel.onResetUiState()
                navActions.navigateToEditProfile()
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
                onPrepareReply = vmContainer.replyViewModel.onPrepareReply,
                onPreparePost = vmContainer.postViewModel.onPreparePost,
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onNavigateToProfile = navActions.navigateToProfile,
                onNavigateToThread = navActions.navigateToThread,
                onNavigateToReply = navActions.navigateToReply,
                onNavigateToPost = navActions.navigateToPost,
            )
        }
        composable(
            route = NozzleRoute.PROFILE_FULL,
            arguments = listOf(navArgument(Identifier.PUBKEY) { type = NavType.StringType })
        ) { backStackEntry ->
            vmContainer.profileViewModel.onSetPubkey(backStackEntry.arguments?.getString(Identifier.PUBKEY))
            ProfileRoute(
                profileViewModel = vmContainer.profileViewModel,
                onPrepareReply = vmContainer.replyViewModel.onPrepareReply,
                onNavigateToThread = navActions.navigateToThread,
                onNavigateToReply = navActions.navigateToReply,
                onNavigateToEditProfile = onNavigateToEditProfile,
            )
        }
        composable(NozzleRoute.SEARCH) {
            SearchRoute(
                searchViewModel = vmContainer.searchViewModel,
                onNavigateToProfile = navActions.navigateToProfile,
                onNavigateToThread = navActions.navigateToThread,
                onGoBack = navActions.popStack,
            )
        }
        composable(NozzleRoute.KEYS) {
            KeysRoute(
                keysViewModel = vmContainer.keysViewModel,
                onResetDrawerUiState = vmContainer.drawerViewModel.onResetUiState,
                onResetFeedIconUiState = vmContainer.feedViewModel.onResetProfileIconUiState,
                onResetEditProfileUiState = vmContainer.editProfileViewModel.onResetUiState,
                onGoBack = navActions.popStack,
            )
        }
        composable(NozzleRoute.EDIT_PROFILE) {
            EditProfileRoute(
                editProfileViewModel = vmContainer.editProfileViewModel,
                onResetDrawerUiState = vmContainer.drawerViewModel.onResetUiState,
                onResetFeedIconUiState = vmContainer.feedViewModel.onResetProfileIconUiState,
                onGoBack = navActions.popStack,
            )
        }
        composable(
            route = NozzleRoute.THREAD_FULL,
            arguments = listOf(
                navArgument(Identifier.POST_ID) { type = NavType.StringType },
                navArgument(Identifier.REPLY_TO_ID) { type = NavType.StringType },
                navArgument(Identifier.REPLY_TO_ROOT_ID) { type = NavType.StringType },
            )
        ) { backStackEntry ->
            vmContainer.threadViewModel.onOpenThread(
                PostIds(
                    id = backStackEntry.arguments?.getString(Identifier.POST_ID).orEmpty(),
                    replyToId = backStackEntry.arguments?.getString(Identifier.REPLY_TO_ID),
                    replyToRootId = backStackEntry.arguments?.getString(Identifier.REPLY_TO_ROOT_ID),
                )
            )
            ThreadRoute(
                threadViewModel = vmContainer.threadViewModel,
                onPrepareReply = vmContainer.replyViewModel.onPrepareReply,
                onNavigateToProfile = navActions.navigateToProfile,
                onNavigateToReply = navActions.navigateToReply,
                onGoBack = navActions.popStack,
            )
        }
        composable(NozzleRoute.REPLY) {
            ReplyRoute(
                replyViewModel = vmContainer.replyViewModel,
                onGoBack = navActions.popStack,
            )
        }
        composable(NozzleRoute.POST) {
            PostRoute(
                postViewModel = vmContainer.postViewModel,
                onGoBack = navActions.popStack,
            )
        }
    }
}
