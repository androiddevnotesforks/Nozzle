package com.kaiwolfram.nozzle.data.utils

import com.kaiwolfram.nozzle.model.*

fun toggleRelay(relays: List<RelayActive>, index: Int): List<RelayActive> {
    return relays.mapIndexed { i, relay ->
        if (index == i) relay.copy(isActive = !relay.isActive) else relay
    }
}

fun listRelayStatuses(
    allRelayUrls: List<String>,
    relaySelection: RelaySelection
): List<RelayActive> {
    return allRelayUrls.map {
        val isActive = when (relaySelection) {
            is AllRelays -> true
            is MultipleRelays -> relaySelection.getSelectedRelays().contains(it)
            is UserSpecific -> relaySelection.getSelectedRelays().contains(it)
        }
        RelayActive(relayUrl = it, isActive = isActive)
    }
}
