package com.kaiwolfram.nozzle.data.preferences.key

import com.kaiwolfram.nostrclientkt.Keys
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider

interface IKeyManager : IPubkeyProvider {
    fun getPrivkey(): String
    fun setPrivkey(privkey: String)
    fun getKeys(): Keys
}
