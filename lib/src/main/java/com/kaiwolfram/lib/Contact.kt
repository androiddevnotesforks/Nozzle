package com.kaiwolfram.lib

import java.net.URL

data class Contact(
    val pubkey: String,
    val relay: URL,
    val alias: String,
)
