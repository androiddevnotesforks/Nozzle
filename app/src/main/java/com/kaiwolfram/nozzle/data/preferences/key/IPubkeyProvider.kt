package com.kaiwolfram.nozzle.data.preferences.key

interface IPubkeyProvider {
    fun getPubkey(): String
    fun getNpub(): String
}
