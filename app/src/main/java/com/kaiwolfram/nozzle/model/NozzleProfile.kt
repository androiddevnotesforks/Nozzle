package com.kaiwolfram.nozzle.model

import androidx.compose.ui.graphics.painter.Painter

data class NozzleProfile(
    val profile: Profile,
    val picture: Painter,
    val numOfFollowing: UInt,
    val numOfFollowers: UInt,
    val posts: List<Post>
)
