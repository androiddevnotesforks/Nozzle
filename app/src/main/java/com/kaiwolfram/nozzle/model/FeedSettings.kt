package com.kaiwolfram.nozzle.model

data class FeedSettings(
    val isPosts: Boolean,
    val isReplies: Boolean,
    val authorSelection: AuthorSelection,
    val relaySelection: RelaySelection,
)
