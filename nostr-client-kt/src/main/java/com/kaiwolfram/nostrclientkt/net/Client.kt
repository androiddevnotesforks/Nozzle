package com.kaiwolfram.nostrclientkt.net

import com.google.gson.JsonElement
import com.kaiwolfram.nostrclientkt.Event
import com.kaiwolfram.nostrclientkt.Filter
import com.kaiwolfram.nostrclientkt.utils.JsonUtils.gson
import okhttp3.*
import java.util.*

class Client {
    private val httpClient = OkHttpClient()
    private val sockets: HashMap<String, WebSocket> = HashMap()
    private val subscriptions: HashMap<String, List<Filter>> = HashMap()
    private val nostrListeners = HashSet<NostrListener>()
    private val socketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            nostrListeners.forEach { it.onOpen(response.message) }
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
            nostrListeners.forEach { it.onClose(reason) }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            nostrListeners.forEach { it.onFailure(t.message) }
        }
    }

    fun subscribe(filters: List<Filter>): String {
        if (filters.isEmpty()) {
            return ""
        }
        val subscriptionId = UUID.randomUUID().toString()
        subscriptions[subscriptionId] = filters
        val request = createSubscriptionRequest(subscriptionId, filters)
        sockets.values.forEach { it.send(request) }

        return subscriptionId
    }

    private fun createSubscriptionRequest(subscriptionId: String, filters: List<Filter>): String {
        return """["REQ",$subscriptionId,${filters.joinToString(",") { it.toJson() }}]"""
    }

    fun unsubscribe(subscriptionId: String) {
        val request = """["CLOSE",$subscriptionId]"""
        sockets.values.forEach { it.send(request) }
        subscriptions.remove(subscriptionId)
    }

    fun publish(event: Event) {
        val request = """["EVENT",${event.toJson()}]"""
        sockets.values.forEach { it.send(request) }
    }

    fun addRelays(urls: List<String>) {
        urls.forEach { addRelay(it) }
    }

    fun addRelay(url: String) {
        if (sockets.containsKey(url)) {
            return
        }
        val request = Request.Builder().url(url).build()
        val socket = httpClient.newWebSocket(request = request, listener = socketListener)
        subscriptions.forEach { (id, filters) ->
            socket.send(
                createSubscriptionRequest(
                    id,
                    filters
                )
            )
        }
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
