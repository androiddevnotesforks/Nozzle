package com.kaiwolfram.lib

data class Post(
    val replyTos: List<String>,
    val mentions: List<String>,
    val msg: String,
)
