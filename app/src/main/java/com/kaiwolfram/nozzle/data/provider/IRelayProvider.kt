package com.kaiwolfram.nozzle.data.provider


interface IRelayProvider {
    fun listRelays(): List<String>
    suspend fun getAutopilotRelays(): Map<String, Set<String>>
}
