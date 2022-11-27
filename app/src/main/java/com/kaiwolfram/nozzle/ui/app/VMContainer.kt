package com.kaiwolfram.nozzle.ui.app

import com.kaiwolfram.nozzle.ui.app.chat.ChatViewModel
import com.kaiwolfram.nozzle.ui.app.feed.FeedViewModel
import com.kaiwolfram.nozzle.ui.app.keys.KeysViewModel
import com.kaiwolfram.nozzle.ui.app.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.relays.RelaysViewModel
import com.kaiwolfram.nozzle.ui.app.support.SupportViewModel

data class VMContainer(
    val profileViewModel: ProfileViewModel,
    val feedViewModel: FeedViewModel,
    val chatViewModel: ChatViewModel,
    val keysViewModel: KeysViewModel,
    val relaysViewModel: RelaysViewModel,
    val supportViewModel: SupportViewModel,
)
