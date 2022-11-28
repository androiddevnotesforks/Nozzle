package com.kaiwolfram.nozzle.model

import androidx.compose.ui.graphics.painter.Painter
import java.time.LocalDateTime

data class Post(
    val author: String,
    var profilePic: Painter,
    val published: LocalDateTime,
    val body: String
)
