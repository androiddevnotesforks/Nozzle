package com.kaiwolfram.nozzle.model


private val defaultRelays = listOf(
    "wss://nos.lol",
    "wss://nostr-pub.wellorder.net",
    "wss://nostr.einundzwanzig.space",
)

fun getDefaultRelays(): List<String> = defaultRelays
