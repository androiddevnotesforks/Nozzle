package com.kaiwolfram.nozzle.ui.app

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Surface
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kaiwolfram.nozzle.AppContainer
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.app.chat.ChatViewModel
import com.kaiwolfram.nozzle.ui.app.feed.FeedViewModel
import com.kaiwolfram.nozzle.ui.app.keys.KeysViewModel
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleDrawer
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavActions
import com.kaiwolfram.nozzle.ui.app.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.relays.RelaysViewModel
import com.kaiwolfram.nozzle.ui.app.support.SupportViewModel
import com.kaiwolfram.nozzle.ui.theme.NozzleTheme
import kotlinx.coroutines.launch

@Composable
fun NozzleApp(appContainer: AppContainer) {
    NozzleTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val vmContainer = VMContainer(
                profileViewModel = viewModel(
                    factory = ProfileViewModel.provideFactory(
                        defaultProfilePicture = painterResource(R.drawable.ic_default_profile),
                        imageLoader = appContainer.imageLoader,
                        context = LocalContext.current
                    )
                ),
                feedViewModel = viewModel(
                    factory = FeedViewModel.provideFactory()
                ),
                chatViewModel = viewModel(
                    factory = ChatViewModel.provideFactory()
                ),
                keysViewModel = viewModel(
                    factory = KeysViewModel.provideFactory()
                ),
                relaysViewModel = viewModel(
                    factory = RelaysViewModel.provideFactory()
                ),
                supportViewModel = viewModel(
                    factory = SupportViewModel.provideFactory()
                ),
            )

            val navController = rememberNavController()
            val navActions = remember(navController) {
                NozzleNavActions(navController)
            }

            val coroutineScope = rememberCoroutineScope()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val profileState by vmContainer.profileViewModel.uiState.collectAsState()

            ModalDrawer(
                drawerState = drawerState,
                drawerContent = {
                    NozzleDrawer(
                        profilePicture = profileState.profilePicture,
                        profileName = profileState.name,
                        navActions = navActions,
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
                        navController = navController
                    )
                }
            }
        }
    }
}
