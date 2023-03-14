package com.kaiwolfram.nozzle.data.provider.impl

import android.util.Log
import com.kaiwolfram.nozzle.data.provider.IAutopilotProvider
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.EventRelayDao
import com.kaiwolfram.nozzle.data.room.dao.Nip65Dao

private const val TAG = "AutopilotProvider"

class AutopilotProvider(
    private val pubkeyProvider: IPubkeyProvider,
    private val nip65Dao: Nip65Dao,
    private val eventRelayDao: EventRelayDao,
) : IAutopilotProvider {

    override suspend fun getAutopilotRelays(pubkeys: Set<String>): Map<String, Set<String>> {
        Log.i(TAG, "Get autopilot relays of ${pubkeys.size} pubkeys")
        if (pubkeys.isEmpty()) return mapOf()

        val processedPubkeys = mutableSetOf<String>()
        val result = mutableListOf<Pair<String, Set<String>>>()

        result.addAll(processNip65(pubkeys))
        result.forEach { processedPubkeys.addAll(it.second) }
        Log.d(TAG, "Processed ${result.size} relays for ${processedPubkeys.size} pubkeys")

        if (pubkeys.size > processedPubkeys.size) {
            val unprocessedPubkeys = pubkeys.minus(processedPubkeys)
            val processedEventRelays = processEventRelays(unprocessedPubkeys)
            result.addAll(processedEventRelays)
            result.forEach { processedPubkeys.addAll(it.second) }
            Log.d(
                TAG,
                "Processed ${processedEventRelays.size} event relays for ${unprocessedPubkeys.size} pubkeys"
            )
        }

        if (pubkeys.size > processedPubkeys.size) {
            val unprocessedPubkeys = pubkeys.minus(processedPubkeys)
            Log.d(TAG, "Default to your read relays for ${unprocessedPubkeys.size} pubkeys")

            nip65Dao.getReadRelaysOfPubkey(pubkey = pubkeyProvider.getPubkey())
                .firstOrNull()
                ?.let {
                    result.add(Pair(it, unprocessedPubkeys.toSet()))
                }
        }

        return mergeResult(result)

    }

    private suspend fun processNip65(pubkeys: Collection<String>): List<Pair<String, Set<String>>> {
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

    private suspend fun processEventRelays(pubkeys: Collection<String>): List<Pair<String, Set<String>>> {
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
            if (it.second.isNotEmpty()) {
                val current = result.putIfAbsent(it.first, it.second.toMutableSet())
                current?.let { _ -> result[it.first]?.addAll(it.second) }
            }
        }
        Log.d(TAG, "${result.map { "${it.key} -> ${it.value.size}\n" }}")

        return result
    }
}
