package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nostrclientkt.Metadata
import kotlinx.coroutines.flow.Flow

interface IPersonalProfileProvider : IPubkeyProvider {
    fun updateMetadata()
    fun getMetadata(): Flow<Metadata?>
}
