package com.kaiwolfram.nozzle.model

import java.time.LocalDateTime

data class Post(
    val author: String,
    val profilePicUrl: String,
    val published: LocalDateTime,
    val content: String
)
