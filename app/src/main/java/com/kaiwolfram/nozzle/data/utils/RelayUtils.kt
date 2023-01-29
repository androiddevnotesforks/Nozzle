package com.kaiwolfram.nozzle.data.utils

import com.kaiwolfram.nozzle.model.RelayActive

fun toggleRelay(relays: List<RelayActive>, index: Int): List<RelayActive> {
    return relays.mapIndexed { i, relay ->
        if (index == i) relay.copy(isActive = !relay.isActive) else relay
    }
}

fun getRelaySelection(allRelayUrls: List<String>, activeRelays: List<String>): List<RelayActive> {
    return allRelayUrls.map {
        RelayActive(relayUrl = it, isActive = activeRelays.contains(it))
    }
}
