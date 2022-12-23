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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kaiwolfram.nozzle.AppContainer
import com.kaiwolfram.nozzle.data.currentProfileCache.IProfileCache
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavActions
import com.kaiwolfram.nozzle.ui.app.views.drawer.NozzleDrawerRoute
import com.kaiwolfram.nozzle.ui.app.views.drawer.NozzleDrawerViewModel
import com.kaiwolfram.nozzle.ui.app.views.feed.FeedViewModel
import com.kaiwolfram.nozzle.ui.app.views.keys.KeysViewModel
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.views.settings.SettingsViewModel
import com.kaiwolfram.nozzle.ui.app.views.thread.ThreadViewModel
import com.kaiwolfram.nozzle.ui.theme.NozzleTheme
import kotlinx.coroutines.launch

@Composable
fun NozzleApp(appContainer: AppContainer) {
    SetProfileCache(
        pubkey = appContainer.keyPreferences.getPubkey(),
        profileDao = appContainer.roomDb.profileDao(),
        profileCache = appContainer.currentProfileCache,
    )
    NozzleTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            val navActions = remember(navController) {
                NozzleNavActions(navController)
            }
            val vmContainer = VMContainer(
                drawerViewModel = viewModel(
                    factory = NozzleDrawerViewModel.provideFactory(
                        currentProfileCache = appContainer.currentProfileCache,
                    )
                ),
                profileViewModel = viewModel(
                    factory = ProfileViewModel.provideFactory(
                        nostrService = appContainer.nostrService,
                        postCardInteractor = appContainer.postCardInteractor,
                        profileFollower = appContainer.profileFollower,
                        currentProfileCache = appContainer.currentProfileCache,
                        profileDao = appContainer.roomDb.profileDao(),
                        eventDao = appContainer.roomDb.eventDao(),
                        context = LocalContext.current,
                        clip = LocalClipboardManager.current,
                    )
                ),
                keysViewModel = viewModel(
                    factory = KeysViewModel.provideFactory(
                        currentProfileCache = appContainer.currentProfileCache,
                        keyManager = appContainer.keyPreferences,
                        context = LocalContext.current,
                        clip = LocalClipboardManager.current,
                    )
                ),
                feedViewModel = viewModel(
                    factory = FeedViewModel.provideFactory(
                        nostrService = appContainer.nostrService,
                        postCardInteractor = appContainer.postCardInteractor,
                        currentProfileCache = appContainer.currentProfileCache,
                    )
                ),
                settingsViewModel = viewModel(
                    factory = SettingsViewModel.provideFactory(
                        currentProfileCache = appContainer.currentProfileCache,
                        profileDao = appContainer.roomDb.profileDao(),
                        context = LocalContext.current,
                    )
                ),
                threadViewModel = viewModel(
                    factory = ThreadViewModel.provideFactory(
                        nostrService = appContainer.nostrService,
                        currentPubkeyReader = appContainer.currentProfileCache,
                        postCardInteractor = appContainer.postCardInteractor,
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
                    NozzleScaffold(
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

// TODO: Use preferences instead of this mess
@Composable
fun SetProfileCache(pubkey: String, profileDao: ProfileDao, profileCache: IProfileCache) {
    LaunchedEffect(null) {
        profileDao.getProfile(pubkey)?.let {
            profileCache.setName(it.name)
            profileCache.setBio(it.bio)
            profileCache.setPictureUrl(it.pictureUrl)
        }
    }
}
