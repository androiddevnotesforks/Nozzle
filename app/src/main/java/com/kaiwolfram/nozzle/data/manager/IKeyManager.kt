package com.kaiwolfram.nozzle.data.manager

import com.kaiwolfram.nostrclientkt.Keys
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider

interface IKeyManager : IPubkeyProvider {
    fun getPrivkey(): String
    fun getNsec(): String
    fun setPrivkey(privkey: String)
    fun getKeys(): Keys
}
