package com.kaiwolfram.nozzle.ui.app

import com.kaiwolfram.nozzle.ui.app.views.drawer.NozzleDrawerViewModel
import com.kaiwolfram.nozzle.ui.app.views.editProfile.EditProfileViewModel
import com.kaiwolfram.nozzle.ui.app.views.feed.FeedViewModel
import com.kaiwolfram.nozzle.ui.app.views.keys.KeysViewModel
import com.kaiwolfram.nozzle.ui.app.views.post.PostViewModel
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.views.reply.ReplyViewModel
import com.kaiwolfram.nozzle.ui.app.views.thread.ThreadViewModel

data class VMContainer(
    val drawerViewModel: NozzleDrawerViewModel,
    val profileViewModel: ProfileViewModel,
    val feedViewModel: FeedViewModel,
    val keysViewModel: KeysViewModel,
    val editProfileViewModel: EditProfileViewModel,
    val threadViewModel: ThreadViewModel,
    val replyViewModel: ReplyViewModel,
    val postViewModel: PostViewModel
)
