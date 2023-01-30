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
    val onNavigateToProfile = remember {
        { pubkey: String ->
            run {
                vmContainer.profileViewModel.onSetPubkey(pubkey)
                navActions.navigateToProfile()
            }
        }
    }
    val onNavigateToThread = remember {
        { postIds: PostIds ->
            run {
                vmContainer.threadViewModel.onOpenThread(postIds)
                navActions.navigateToThread()
            }
        }
    }
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
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToThread = onNavigateToThread,
                onNavigateToReply = navActions.navigateToReply,
                onNavigateToPost = navActions.navigateToPost,
            )
        }
        composable(NozzleRoute.PROFILE) {
            ProfileRoute(
                profileViewModel = vmContainer.profileViewModel,
                onPrepareReply = vmContainer.replyViewModel.onPrepareReply,
                onNavigateToThread = onNavigateToThread,
                onNavigateToReply = navActions.navigateToReply,
                onNavigateToEditProfile = onNavigateToEditProfile,
            )
        }
        composable(NozzleRoute.SEARCH) {
            SearchRoute(
                searchViewModel = vmContainer.searchViewModel,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToThread = onNavigateToThread,
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
        composable(NozzleRoute.THREAD) {
            ThreadRoute(
                threadViewModel = vmContainer.threadViewModel,
                onPrepareReply = vmContainer.replyViewModel.onPrepareReply,
                onNavigateToProfile = onNavigateToProfile,
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
