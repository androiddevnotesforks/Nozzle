package com.kaiwolfram.nostrclientkt

import java.net.URL

data class Contact(
    val pubkey: String,
    val relay: URL,
    val alias: String,
)
