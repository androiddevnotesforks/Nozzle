package com.kaiwolfram.nozzle.model

import com.kaiwolfram.nostrclientkt.model.RelaySelection

data class FeedSettings(
    val isPosts: Boolean,
    val isReplies: Boolean,
    val authorSelection: AuthorSelection,
    val relaySelection: RelaySelection,
)
