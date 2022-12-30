package com.kaiwolfram.nozzle.data.nostr

import com.kaiwolfram.nostrclientkt.Metadata

data class Profile(
    val pubkey: String,
    val metadata: Metadata,
)
