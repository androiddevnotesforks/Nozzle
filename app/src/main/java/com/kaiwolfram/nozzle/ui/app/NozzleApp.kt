package com.kaiwolfram.nozzle.ui.app

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Surface
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kaiwolfram.nozzle.AppContainer
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavActions
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavGraph
import com.kaiwolfram.nozzle.ui.app.views.drawer.NozzleDrawerRoute
import com.kaiwolfram.nozzle.ui.app.views.drawer.NozzleDrawerViewModel
import com.kaiwolfram.nozzle.ui.app.views.editProfile.EditProfileViewModel
import com.kaiwolfram.nozzle.ui.app.views.feed.FeedViewModel
import com.kaiwolfram.nozzle.ui.app.views.keys.KeysViewModel
import com.kaiwolfram.nozzle.ui.app.views.post.PostViewModel
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.views.reply.ReplyViewModel
import com.kaiwolfram.nozzle.ui.app.views.thread.ThreadViewModel
import com.kaiwolfram.nozzle.ui.theme.NozzleTheme
import kotlinx.coroutines.launch

@Composable
fun NozzleApp(appContainer: AppContainer) {
    NozzleTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            val navActions = remember(navController) {
                NozzleNavActions(navController)
            }
            val vmContainer = VMContainer(
                drawerViewModel = viewModel(
                    factory = NozzleDrawerViewModel.provideFactory(
                        profileProvider = appContainer.profileCache,
                    )
                ),
                profileViewModel = viewModel(
                    factory = ProfileViewModel.provideFactory(
                        nostrService = appContainer.nostrService,
                        postCardInteractor = appContainer.postCardInteractor,
                        feedProvider = appContainer.feedProvider,
                        profileFollower = appContainer.profileFollower,
                        pubkeyProvider = appContainer.profileCache,
                        profileDao = appContainer.roomDb.profileDao(),
                        context = LocalContext.current,
                        clip = LocalClipboardManager.current,
                    )
                ),
                keysViewModel = viewModel(
                    factory = KeysViewModel.provideFactory(
                        profileCache = appContainer.profileCache,
                        keyManager = appContainer.keyPreferences,
                        context = LocalContext.current,
                        clip = LocalClipboardManager.current,
                    )
                ),
                feedViewModel = viewModel(
                    factory = FeedViewModel.provideFactory(
                        profileProvider = appContainer.profileCache,
                        feedProvider = appContainer.feedProvider,
                        postCardInteractor = appContainer.postCardInteractor,
                    )
                ),
                editProfileViewModel = viewModel(
                    factory = EditProfileViewModel.provideFactory(
                        profileCache = appContainer.profileCache,
                        profileDao = appContainer.roomDb.profileDao(),
                        nostrService = appContainer.nostrService,
                        context = LocalContext.current,
                    )
                ),
                threadViewModel = viewModel(
                    factory = ThreadViewModel.provideFactory(
                        threadProvider = appContainer.threadProvider,
                        postCardInteractor = appContainer.postCardInteractor,
                    )
                ),
                replyViewModel = viewModel(
                    factory = ReplyViewModel.provideFactory(
                        nostrService = appContainer.nostrService,
                        profileProvider = appContainer.profileCache,
                        context = LocalContext.current,
                    )
                ),
                postViewModel = viewModel(
                    factory = PostViewModel.provideFactory(
                        nostrService = appContainer.nostrService,
                        profileProvider = appContainer.profileCache,
                        context = LocalContext.current,
                    )
                )
            )

            val coroutineScope = rememberCoroutineScope()
            val drawerState = rememberDrawerState(DrawerValue.Closed)

            ModalDrawer(
                drawerState = drawerState,
                drawerContent = {
                    NozzleDrawerRoute(
                        nozzleDrawerViewModel = vmContainer.drawerViewModel,
                        navActions = navActions,
                        onSetPubkey = vmContainer.profileViewModel.onSetPubkey,
                        closeDrawer = { coroutineScope.launch { drawerState.close() } },
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                    )
                },
            ) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    NozzleNavGraph(
                        vmContainer = vmContainer,
                        navController = navController,
                        navActions = navActions,
                        drawerState = drawerState,
                    )
                }
            }
        }
    }
}
