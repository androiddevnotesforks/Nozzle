package com.kaiwolfram.nozzle.data.provider

interface IAutopilotProvider {
    suspend fun getAutopilotRelays(pubkeys: Set<String>): Map<String, Set<String>>
}
