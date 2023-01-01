package com.kaiwolfram.nostrclientkt.net

import android.util.Log
import com.google.gson.JsonElement
import com.kaiwolfram.nostrclientkt.Event
import com.kaiwolfram.nostrclientkt.Filter
import com.kaiwolfram.nostrclientkt.utils.JsonUtils.gson
import okhttp3.*
import java.util.*

private const val TAG = "Client"

class Client {
    private val httpClient = OkHttpClient()
    private val sockets: HashMap<String, WebSocket> = HashMap()
    private val subscriptions: HashMap<String, List<Filter>> = HashMap()
    private var nostrListener: NostrListener? = null
    private val baseListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.i(TAG, "onOpen: $response")
            nostrListener?.onOpen(response.message)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.i(TAG, "onMessage: $text")
            try {
                val msg = gson.fromJson(text, JsonElement::class.java).asJsonArray
                val type = msg[0].asString
                when (type) {
                    "EVENT" -> {
                        Event.fromJson(msg[2]).onSuccess { event ->
                            nostrListener?.onEvent(
                                subscriptionId = msg[1].asString,
                                event = event
                            )
                        }
                    }
                    "NOTICE" -> nostrListener?.onError(msg = msg[1].asString)
                    "EOSE" -> nostrListener?.onEOSE(subscriptionId = msg[1].asString)
                    else -> nostrListener?.onError(msg = "Unknown type $type. Msg was $text")

                }
            } catch (t: Throwable) {
                nostrListener?.onError("Problem with $text, $t")
                nostrListener?.onError("Queue size ${webSocket.queueSize()}")
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.i(TAG, "onClosing: $reason, $code")
            nostrListener?.onClose(reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.i(TAG, "onFailure: $t, $response")
            nostrListener?.onFailure(t.message)
        }
    }

    fun subscribe(filters: List<Filter>): String {
        if (filters.isEmpty()) {
            return ""
        }
        val subscriptionId = UUID.randomUUID().toString()
        subscriptions[subscriptionId] = filters
        val request = createSubscriptionRequest(subscriptionId, filters)
        Log.i(TAG, "Send $request to ${sockets.values.size} relays")
        sockets.values.forEach { it.send(request) }

        Log.i(TAG, "Subscribe to $subscriptionId")

        return subscriptionId
    }

    private fun createSubscriptionRequest(subscriptionId: String, filters: List<Filter>): String {
        return """["REQ","$subscriptionId",${filters.joinToString(",") { it.toJson() }}]"""
    }

    fun unsubscribe(subscriptionId: String) {
        Log.i(TAG, "Unsubscribe from $subscriptionId")

        val request = """["CLOSE",$subscriptionId]"""
        sockets.values.forEach { it.send(request) }
        subscriptions.remove(subscriptionId)
    }

    fun publish(event: Event) {
        Log.i(TAG, "Publish event $event")

        val request = """["EVENT",${event.toJson()}]"""
        sockets.values.forEach { it.send(request) }
    }

    fun addRelays(urls: List<String>) {
        urls.forEach { addRelay(it) }
    }

    fun addRelay(url: String) {
        Log.i(TAG, "Add relay $url")

        if (sockets.containsKey(url)) {
            return
        }
        val request = Request.Builder().url(url).build()
        val socket = httpClient.newWebSocket(request = request, listener = baseListener)
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
        Log.i(TAG, "Remove relay $url")
        sockets[url]?.close(1000, "Normal closure")
    }

    fun setListener(listener: NostrListener) {
        Log.i(TAG, "Set listener")

        nostrListener = listener
    }

    fun removeListener() {
        Log.i(TAG, "Remove listener")

        nostrListener = null
    }

    fun close() {
        Log.i(TAG, "Close connections")
        sockets.keys.forEach { removeRelay(it) }
        httpClient.dispatcher.executorService.shutdown()
    }
}
