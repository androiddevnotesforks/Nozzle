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
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kaiwolfram.nozzle.AppContainer
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavActions
import com.kaiwolfram.nozzle.ui.app.views.chat.ChatViewModel
import com.kaiwolfram.nozzle.ui.app.views.drawer.NozzleDrawerRoute
import com.kaiwolfram.nozzle.ui.app.views.drawer.NozzleDrawerViewModel
import com.kaiwolfram.nozzle.ui.app.views.feed.FeedViewModel
import com.kaiwolfram.nozzle.ui.app.views.keys.KeysViewModel
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.views.relays.RelaysViewModel
import com.kaiwolfram.nozzle.ui.app.views.settings.SettingsViewModel
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
            val defaultProfilePicture = painterResource(R.drawable.ic_default_profile)

            val vmContainer = VMContainer(
                profileViewModel = viewModel(
                    factory = ProfileViewModel.provideFactory(
                        defaultProfilePicture = defaultProfilePicture,
                        nostrRepository = appContainer.nostrRepository,
                        pictureRequester = appContainer.pictureRequester,
                        context = LocalContext.current,
                        clip = LocalClipboardManager.current,
                        profileDao = appContainer.roomDb.profileDao(),
                        eventDao = appContainer.roomDb.eventDao(),
                    )
                ),
                feedViewModel = viewModel(
                    factory = FeedViewModel.provideFactory()
                ),
                chatViewModel = viewModel(
                    factory = ChatViewModel.provideFactory()
                ),
                keysViewModel = viewModel(
                    factory = KeysViewModel.provideFactory(
                        profilePreferences = appContainer.profilePreferences,
                        context = LocalContext.current,
                        clip = LocalClipboardManager.current,
                    )
                ),
                relaysViewModel = viewModel(
                    factory = RelaysViewModel.provideFactory()
                ),
                settingsViewModel = viewModel(
                    factory = SettingsViewModel.provideFactory()
                ),
            )

            val coroutineScope = rememberCoroutineScope()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val nozzleDrawerViewModel: NozzleDrawerViewModel = viewModel(
                factory = NozzleDrawerViewModel.provideFactory(
                    defaultProfilePicture = defaultProfilePicture,
                    nostrRepository = appContainer.nostrRepository,
                    pictureRequester = appContainer.pictureRequester,
                    profilePreferences = appContainer.profilePreferences,
                )
            )

            ModalDrawer(
                drawerState = drawerState,
                drawerContent = {
                    NozzleDrawerRoute(
                        nozzleDrawerViewModel = nozzleDrawerViewModel,
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
                    )
                }
            }
        }
    }
}
