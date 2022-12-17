package com.kaiwolfram.nozzle.model

import androidx.compose.ui.graphics.painter.Painter

data class PostWithMeta(
    val name: String,
    val picture: Painter,
    val pubkey: String,
    val createdAt: Long,
    val content: String,
)
