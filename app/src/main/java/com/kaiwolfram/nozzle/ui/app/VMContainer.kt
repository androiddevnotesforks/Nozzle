package com.kaiwolfram.nozzle.ui.app

import com.kaiwolfram.nozzle.ui.app.feed.FeedViewModel
import com.kaiwolfram.nozzle.ui.app.messages.MessagesViewModel
import com.kaiwolfram.nozzle.ui.app.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.search.SearchViewModel

data class VMContainer(
    val profileViewModel: ProfileViewModel,
    val feedViewModel: FeedViewModel,
    val searchViewModel: SearchViewModel,
    val messagesViewModel: MessagesViewModel
)
