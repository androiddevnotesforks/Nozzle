package com.kaiwolfram.nozzle.ui.app

import com.kaiwolfram.nozzle.ui.app.views.chat.ChatViewModel
import com.kaiwolfram.nozzle.ui.app.views.feed.FeedViewModel
import com.kaiwolfram.nozzle.ui.app.views.keys.KeysViewModel
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.views.profile.edit.EditProfileViewModel
import com.kaiwolfram.nozzle.ui.app.views.relays.RelaysViewModel

data class VMContainer(
    val profileViewModel: ProfileViewModel,
    val editProfileViewModel: EditProfileViewModel,
    val feedViewModel: FeedViewModel,
    val chatViewModel: ChatViewModel,
    val keysViewModel: KeysViewModel,
    val relaysViewModel: RelaysViewModel,
)
