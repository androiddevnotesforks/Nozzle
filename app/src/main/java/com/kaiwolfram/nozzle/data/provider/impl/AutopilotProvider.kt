package com.kaiwolfram.nozzle.data.provider.impl

import android.util.Log
import com.kaiwolfram.nozzle.data.getDefaultRelays
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

        val result = mutableListOf<Pair<String, Set<String>>>()
        val processedPubkeys = mutableSetOf<String>()

        processNip65(result = result, processedPubkeys = processedPubkeys, pubkeys = pubkeys)

        if (pubkeys.size > processedPubkeys.size) {
            processEventRelays(
                result = result,
                processedPubkeys = processedPubkeys,
                pubkeys = pubkeys.minus(processedPubkeys)
            )
        }

        if (pubkeys.size > processedPubkeys.size) {
            processDefault(
                result = result,
                processedPubkeys = processedPubkeys,
                pubkeys = pubkeys.minus(processedPubkeys)
            )
        }

        if (pubkeys.size > processedPubkeys.size) {
            Log.w(TAG, "Failed to process all pubkeys")
        }

        return mergeResult(result)

    }

    private suspend fun processNip65(
        result: MutableList<Pair<String, Set<String>>>,
        processedPubkeys: MutableSet<String>,
        pubkeys: Collection<String>
    ) {
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
        Log.d(TAG, "Processed ${result.size} nip65 relays for ${processedPubkeys.size} pubkeys")
    }

    private suspend fun processEventRelays(
        result: MutableList<Pair<String, Set<String>>>,
        processedPubkeys: MutableSet<String>,
        pubkeys: Collection<String>
    ) {
        val newlyProcessedPubkeys = mutableSetOf<String>()
        val newlyProcessedEventRelays = mutableListOf<Pair<String, Set<String>>>()

        eventRelayDao.getPubkeysPerRelayMap(pubkeys = pubkeys)
            .toList()
            .sortedByDescending { it.second.size }
            .forEach {
                val pubkeysToAdd = mutableSetOf<String>()
                pubkeysToAdd.addAll(it.second.minus(processedPubkeys).minus(newlyProcessedPubkeys))
                if (pubkeysToAdd.isNotEmpty()) {
                    newlyProcessedPubkeys.addAll(pubkeysToAdd)
                    newlyProcessedEventRelays.add(Pair(it.first, pubkeysToAdd))
                }
            }

        processedPubkeys.addAll(newlyProcessedPubkeys)
        result.addAll(newlyProcessedEventRelays)

        Log.d(
            TAG,
            "Processed ${newlyProcessedEventRelays.size} event relays for ${
                newlyProcessedPubkeys.size
            } pubkeys"
        )
    }

    private suspend fun processDefault(
        result: MutableList<Pair<String, Set<String>>>,
        processedPubkeys: MutableSet<String>,
        pubkeys: Collection<String>
    ) {
        Log.d(TAG, "Default to your read relays for ${pubkeys.size} pubkeys")

        var personalReadRelays = nip65Dao.getReadRelaysOfPubkey(
            pubkey = pubkeyProvider.getPubkey()
        )
        if (personalReadRelays.isEmpty()) personalReadRelays = getDefaultRelays()

        personalReadRelays.firstOrNull()
            ?.let {
                result.add(Pair(it, pubkeys.toSet()))
                processedPubkeys.addAll(pubkeys)
            }
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
