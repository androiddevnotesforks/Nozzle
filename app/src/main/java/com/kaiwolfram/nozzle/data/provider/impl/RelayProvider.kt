package com.kaiwolfram.nozzle.data.provider.impl

import com.kaiwolfram.nozzle.data.provider.IAutopilotProvider
import com.kaiwolfram.nozzle.data.provider.IContactListProvider
import com.kaiwolfram.nozzle.data.provider.IRelayProvider
import com.kaiwolfram.nozzle.model.getDefaultRelays
import java.util.*


class RelayProvider(
    private val autopilotProvider: IAutopilotProvider,
    private val contactListProvider: IContactListProvider,
) : IRelayProvider {

    private val cache: MutableList<String> = Collections.synchronizedList(mutableListOf())

    init {
        cache.addAll(getDefaultRelays())
    }

    // TODO: Show your nip65 relays or default if empty
    override fun listRelays(): List<String> {
        return cache
    }

    override suspend fun getAutopilotRelays(): Map<String, Set<String>> {
        val contacts = contactListProvider.listPersonalContactPubkeys().toSet()
        val result = autopilotProvider.getAutopilotRelays(pubkeys = contacts)

        val newRelays = result.keys.minus(cache.toSet())
        if (newRelays.isNotEmpty()) cache.addAll(0, newRelays)

        return result
    }
}
