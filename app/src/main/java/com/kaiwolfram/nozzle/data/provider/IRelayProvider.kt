package com.kaiwolfram.nozzle.data.provider

import kotlinx.coroutines.flow.Flow


interface IRelayProvider {
    fun listRelays(): Flow<List<String>>
}
