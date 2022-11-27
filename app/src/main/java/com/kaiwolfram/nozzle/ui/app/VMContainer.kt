package com.kaiwolfram.nozzle.ui.app

import com.kaiwolfram.nozzle.ui.app.chat.ChatViewModel
import com.kaiwolfram.nozzle.ui.app.feed.FeedViewModel
import com.kaiwolfram.nozzle.ui.app.profile.ProfileViewModel

data class VMContainer(
    val profileViewModel: ProfileViewModel,
    val feedViewModel: FeedViewModel,
    val chatViewModel: ChatViewModel
)
