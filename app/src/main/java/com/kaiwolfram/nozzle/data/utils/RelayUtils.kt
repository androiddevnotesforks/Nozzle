package com.kaiwolfram.nozzle.data.utils

import com.kaiwolfram.nostrclientkt.model.*
import com.kaiwolfram.nozzle.model.RelayActive

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
            is Autopilot -> true // TODO: Use AutopilotProvider
            is PersonalNip65 -> true // TODO: Use your relays
            is MultipleRelays -> relaySelection.relays.contains(it)
        }
        RelayActive(relayUrl = it, isActive = isActive)
    }
}
