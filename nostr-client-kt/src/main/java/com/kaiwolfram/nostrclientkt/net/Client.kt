package com.kaiwolfram.nostrclientkt.net

import com.google.gson.JsonElement
import com.kaiwolfram.nostrclientkt.Event
import com.kaiwolfram.nostrclientkt.Filter
import com.kaiwolfram.nostrclientkt.gson
import okhttp3.*
import java.util.*

class Client {
    private val httpClient = OkHttpClient()
    private val sockets: HashMap<String, WebSocket> = HashMap()
    private val nostrListeners = HashSet<NostrListener>()
    private val socketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            nostrListeners.forEach { it.onOpen() }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val msg = gson.fromJson(text, JsonElement::class.java).asJsonArray
                val type = msg[0].asString
                when (type) {
                    "EVENT" -> {
                        Event.fromJson(msg[2]).onSuccess { event ->
                            nostrListeners.forEach {
                                it.onEvent(
                                    subscriptionId = msg[1].asString,
                                    event = event
                                )
                            }
                        }
                    }
                    "NOTICE" -> nostrListeners.forEach {
                        it.onError(msg = msg[1].asString)
                    }
                    "EOSE" -> nostrListeners.forEach {
                        it.onEOSE(subscriptionId = msg[1].asString)
                    }
                    else -> nostrListeners.forEach {
                        it.onError(msg = "Unknown type $type. Msg was $text")
                    }
                }
            } catch (t: Throwable) {
                nostrListeners.forEach { it.onError("Problem with $text") }
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            nostrListeners.forEach { it.onClose() }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            nostrListeners.forEach { it.onFailure() }
        }
    }

    fun subscribe(filters: List<Filter>): String {
        val subscriptionId = UUID.randomUUID().toString()
        val request = """["REQ",$subscriptionId,${filters.joinToString(",") { it.toJson() }}]"""
        sockets.values.forEach { it.send(request) }

        return subscriptionId
    }

    fun unsubscribe(subscriptionId: String) {
        val request = """["CLOSE",$subscriptionId]"""
        sockets.values.forEach { it.send(request) }
    }

    fun publish(event: Event) {
        val request = """["EVENT",${event.toJson()}]"""
        sockets.values.forEach { it.send(request) }
    }

    fun addRelay(url: String) {
        if (sockets.containsKey(url)) {
            return
        }
        val request = Request.Builder().url(url).build()
        val socket = httpClient.newWebSocket(request = request, listener = socketListener)
        sockets[url] = socket
    }

    fun removeRelay(url: String) {
        sockets[url]?.close(1000, "Normal closure")
    }

    fun register(listener: NostrListener) {
        nostrListeners.add(listener)
    }

    fun unregister(listener: NostrListener) {
        nostrListeners.remove(listener)
    }

    fun close() {
        sockets.keys.forEach { removeRelay(it) }
        httpClient.dispatcher.executorService.shutdown()
    }
}
