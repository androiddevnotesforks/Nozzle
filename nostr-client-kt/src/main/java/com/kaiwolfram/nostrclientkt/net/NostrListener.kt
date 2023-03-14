package com.kaiwolfram.nostrclientkt.net

import com.kaiwolfram.nostrclientkt.model.Event

interface NostrListener {
    fun onOpen(msg: String)
    fun onEvent(subscriptionId: String, event: Event, relayUrl: String?)
    fun onError(msg: String, throwable: Throwable? = null)
    fun onEOSE(subscriptionId: String)

    // TODO: remove socket from hashmap
    fun onClose(reason: String)
    fun onFailure(msg: String?, throwable: Throwable? = null)
    fun onOk(id: String)
}
