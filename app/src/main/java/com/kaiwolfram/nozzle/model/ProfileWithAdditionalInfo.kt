package com.kaiwolfram.nozzle.model

import com.kaiwolfram.nostrclientkt.model.Metadata

data class ProfileWithAdditionalInfo(
    val pubkey: String,
    val npub: String,
    val metadata: Metadata,
    val numOfFollowing: Int,
    val numOfFollowers: Int,
    val relays: List<String>,
    val isOneself: Boolean,
    val isFollowedByMe: Boolean,
)
