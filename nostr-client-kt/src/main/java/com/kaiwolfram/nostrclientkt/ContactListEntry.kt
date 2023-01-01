package com.kaiwolfram.nostrclientkt

data class ContactListEntry(
    val pubkey: String,
    val relayUrl: String = "",
    val petname: String = "",
)
