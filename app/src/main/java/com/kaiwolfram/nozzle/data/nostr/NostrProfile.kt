package com.kaiwolfram.nozzle.data.nostr

data class NostrProfile(
    val pubkey: String,
    val name: String,
    val about: String,
    val picture: String,
)
