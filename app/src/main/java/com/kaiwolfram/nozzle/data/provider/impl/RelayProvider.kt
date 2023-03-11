package com.kaiwolfram.nozzle.data.provider.impl

import com.kaiwolfram.nozzle.data.provider.IRelayProvider


class RelayProvider() : IRelayProvider {
    private val defaultRelays = listOf(
        "wss://nos.lol",
        "wss://nostr-pub.wellorder.net",
        "wss://nostr.einundzwanzig.space",
    )

    // TODO: Show your nip65 relays or default if empty
    override fun listRelays(): List<String> {
        return defaultRelays
    }
}
