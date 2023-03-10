package com.kaiwolfram.nozzle.data.provider

interface IAutopilotProvider {
    suspend fun getAutopilotRelays(): Map<String, Set<String>>
}
