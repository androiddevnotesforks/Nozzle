package com.kaiwolfram.nozzle.ui.app

import com.kaiwolfram.nozzle.ui.app.views.drawer.NozzleDrawerViewModel
import com.kaiwolfram.nozzle.ui.app.views.feed.FeedViewModel
import com.kaiwolfram.nozzle.ui.app.views.keys.KeysViewModel
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.views.relays.RelaysViewModel
import com.kaiwolfram.nozzle.ui.app.views.settings.SettingsViewModel

data class VMContainer(
    val drawerViewModel: NozzleDrawerViewModel,
    val profileViewModel: ProfileViewModel,
    val feedViewModel: FeedViewModel,
    val keysViewModel: KeysViewModel,
    val relaysViewModel: RelaysViewModel,
    val settingsViewModel: SettingsViewModel,
)
