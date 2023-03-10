package com.kaiwolfram.nozzle.data.provider.impl

import android.util.Log
import com.kaiwolfram.nozzle.data.provider.IAutopilotProvider
import com.kaiwolfram.nozzle.data.provider.IContactListProvider
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.EventRelayDao
import com.kaiwolfram.nozzle.data.room.dao.Nip65Dao

private const val TAG = "AutopilotProvider"

class AutopilotProvider(
    private val contactListProvider: IContactListProvider,
    private val pubkeyProvider: IPubkeyProvider,
    private val nip65Dao: Nip65Dao,
    private val eventRelayDao: EventRelayDao,
) : IAutopilotProvider {

    override suspend fun getAutopilotRelays(): Map<String, Set<String>> {
        Log.i(TAG, "Get autopilot relays")
        val contacts = contactListProvider.listPersonalContactPubkeys()
        if (contacts.isEmpty()) return mapOf()

        val processedPubkeys = mutableSetOf<String>()
        val result = mutableListOf<Pair<String, Set<String>>>()

        result.addAll(processNip65(contacts))
        result.forEach { processedPubkeys.addAll(it.second) }
        Log.d(TAG, "Processed ${result.size} relays for ${processedPubkeys.size} pubkeys")

        if (contacts.size > processedPubkeys.size) {
            val unprocessedPubkeys = contacts.minus(processedPubkeys)
            val processedEventRelays = processEventRelays(unprocessedPubkeys)
            result.addAll(processedEventRelays)
            result.forEach { processedPubkeys.addAll(it.second) }
            Log.d(
                TAG,
                "Processed ${processedEventRelays.size} event relays for ${unprocessedPubkeys.size} pubkeys"
            )
        }

        if (contacts.size > processedPubkeys.size) {
            val unprocessedPubkeys = contacts.minus(processedPubkeys)
            Log.d(TAG, "Default to your read relays for ${unprocessedPubkeys.size} pubkeys")

            nip65Dao.getReadRelaysOfPubkey(pubkey = pubkeyProvider.getPubkey())
                .firstOrNull()
                ?.let {
                    result.add(Pair(it, unprocessedPubkeys.toSet()))
                }
        }

        return mergeResult(result)

    }

    private suspend fun processNip65(pubkeys: List<String>): List<Pair<String, Set<String>>> {
        val processedPubkeys = mutableSetOf<String>()
        val result = mutableListOf<Pair<String, Set<String>>>()

        nip65Dao.getPubkeysPerWriteRelayMap(pubkeys = pubkeys)
            .toList()
            .sortedByDescending { it.second.size }
            .forEach {
                val pubkeysToAdd = mutableSetOf<String>()
                pubkeysToAdd.addAll(it.second.minus(processedPubkeys))
                if (pubkeysToAdd.isNotEmpty()) {
                    processedPubkeys.addAll(pubkeysToAdd)
                    result.add(Pair(it.first, pubkeysToAdd))
                }
            }

        return result
    }

    private suspend fun processEventRelays(pubkeys: List<String>): List<Pair<String, Set<String>>> {
        Log.d(TAG, "Default to event relays for ${pubkeys.size} pubkeys")
        val processedPubkeys = mutableSetOf<String>()
        val result = mutableListOf<Pair<String, Set<String>>>()

        eventRelayDao.getPubkeysPerRelayMap(pubkeys = pubkeys)
            .toList()
            .sortedByDescending { it.second.size }
            .forEach {
                val pubkeysToAdd = mutableSetOf<String>()
                pubkeysToAdd.addAll(it.second.minus(processedPubkeys))
                if (pubkeysToAdd.isNotEmpty()) {
                    processedPubkeys.addAll(pubkeysToAdd)
                    result.add(Pair(it.first, pubkeysToAdd))
                }
            }

        return result
    }

    private fun mergeResult(toMerge: List<Pair<String, Set<String>>>): Map<String, Set<String>> {
        val result = mutableMapOf<String, MutableSet<String>>()
        toMerge.forEach {
            val current = result.putIfAbsent(it.first, it.second.toMutableSet())
            current?.let { _ -> result[it.first]?.addAll(it.second) }
        }

        return result
    }
}
