package com.kaiwolfram.nozzle.model

import androidx.compose.ui.graphics.painter.Painter
import java.time.LocalDateTime

data class Post(
    val author: String,
    val profilePicUrl: String,
    val published: LocalDateTime,
    val content: String
)
