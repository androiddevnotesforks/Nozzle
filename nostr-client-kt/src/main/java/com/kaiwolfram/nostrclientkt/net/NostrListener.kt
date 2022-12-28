package com.kaiwolfram.nostrclientkt.net

import com.kaiwolfram.nostrclientkt.Event

interface NostrListener {
    fun onOpen()
    fun onEvent(subscriptionId: String, event: Event)
    fun onError(msg: String)
    fun onEOSE(subscriptionId: String)
    fun onClose()
    fun onFailure()
}
