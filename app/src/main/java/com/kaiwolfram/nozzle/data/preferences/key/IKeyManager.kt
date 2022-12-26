package com.kaiwolfram.nozzle.data.preferences.key

interface IKeyManager : IPubkeyProvider {
    fun getPrivkey(): String
    fun setPrivkey(privkey: String)
}
