package com.kaiwolfram.nozzle.data.preferences.key

import com.kaiwolfram.nostrclientkt.Keys

interface IKeyManager : IPubkeyProvider {
    fun getPrivkey(): String
    fun setPrivkey(privkey: String)
    fun getKeys(): Keys
}
