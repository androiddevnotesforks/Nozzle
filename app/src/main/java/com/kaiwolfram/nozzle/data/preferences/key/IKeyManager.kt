package com.kaiwolfram.nozzle.data.preferences.key

interface IKeyManager : IPubkeyReader {
    fun getPrivkey(): String
    fun setPrivkey(privkey: String)
}
