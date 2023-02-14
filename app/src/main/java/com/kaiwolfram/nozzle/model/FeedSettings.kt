package com.kaiwolfram.nozzle.model

data class FeedSettings(
    val isContactsOnly: Boolean,
    val isPosts: Boolean,
    val isReplies: Boolean,
    val relays: List<String>,
)
