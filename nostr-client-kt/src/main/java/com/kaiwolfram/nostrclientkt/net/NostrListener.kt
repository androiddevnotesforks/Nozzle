package com.kaiwolfram.nostrclientkt.net

import com.kaiwolfram.nostrclientkt.Event

interface NostrListener {
    fun onOpen(msg: String)
    fun onEvent(subscriptionId: String, event: Event)
    fun onError(msg: String)
    fun onEOSE(subscriptionId: String)
    fun onClose(reason: String)
    fun onFailure(msg: String?)
}
