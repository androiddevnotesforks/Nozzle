package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nostrclientkt.Metadata

interface IPersonalProfileProvider : IPubkeyProvider {
    suspend fun getMetadata(): Metadata?
}
