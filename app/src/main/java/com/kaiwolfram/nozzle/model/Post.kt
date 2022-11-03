package com.kaiwolfram.nozzle.model

import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDateTime

data class Post(
    val author: String,
    val profilePic: ImageVector,
    val published: LocalDateTime,
    val body: String
)
