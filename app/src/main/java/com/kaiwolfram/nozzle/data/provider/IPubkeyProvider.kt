package com.kaiwolfram.nozzle.data.provider

interface IPubkeyProvider {
    fun getPubkey(): String
    fun getNpub(): String
}
