package com.kaiwolfram.nostrclientkt

data class Post(
    val replyTos: List<String>,
    val relayUrl: String,
    val mentions: List<String>,
    val msg: String,
)
