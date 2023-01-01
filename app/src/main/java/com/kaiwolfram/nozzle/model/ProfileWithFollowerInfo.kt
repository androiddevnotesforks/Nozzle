package com.kaiwolfram.nozzle.model

import com.kaiwolfram.nostrclientkt.Metadata

data class ProfileWithFollowerInfo(
    val pubkey: String,
    val npub: String,
    val metadata: Metadata,
    val numOfFollowing: Int,
    val numOfFollowers: Int,
    val isOneself: Boolean,
    val isFollowedByMe: Boolean,
)
