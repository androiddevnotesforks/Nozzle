package com.kaiwolfram.nostrclientkt.net

import android.util.Log
import com.google.gson.JsonElement
import com.kaiwolfram.nostrclientkt.model.Event
import com.kaiwolfram.nostrclientkt.model.Filter
import com.kaiwolfram.nostrclientkt.utils.JsonUtils.gson
import okhttp3.*
import java.util.*

private const val TAG = "Client"

class Client {
    private val httpClient = OkHttpClient()
    private val sockets: HashMap<String, WebSocket> = HashMap()
    private val subscriptions: HashMap<String, WebSocket> = HashMap()
    private var nostrListener: NostrListener? = null
    private val baseListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            nostrListener?.onOpen(response.message)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val msg = gson.fromJson(text, JsonElement::class.java).asJsonArray
                val type = msg[0].asString
                when (type) {
                    "EVENT" -> {
                        Event.fromJson(msg[2]).onSuccess { event ->
                            nostrListener?.onEvent(
                                subscriptionId = msg[1].asString,
                                event = event,
                                relayUrl = getRelayUrl(webSocket)
                            )
                        }
                    }
                    "OK" -> nostrListener?.onOk(id = msg[1].asString.orEmpty())
                    "NOTICE" -> nostrListener?.onError(msg = msg[1].asString)
                    "EOSE" -> nostrListener?.onEOSE(subscriptionId = msg[1].asString)
                    else -> nostrListener?.onError(msg = "Unknown type $type. Msg was $text")

                }
            } catch (t: Throwable) {
                nostrListener?.onError("Problem with $text", t)
                nostrListener?.onError("Queue size ${webSocket.queueSize()}", t)
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            nostrListener?.onClose(reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            nostrListener?.onFailure(response?.message, t)
        }
    }

    fun subscribe(filters: List<Filter>): List<String> {
        if (filters.isEmpty()) {
            return listOf()
        }
        val ids = mutableListOf<String>()
        sockets.values.forEach {
            val subscriptionId = UUID.randomUUID().toString()
            ids.add(subscriptionId)
            subscriptions[subscriptionId] = it
            val request = createSubscriptionRequest(subscriptionId, filters)
            Log.i(TAG, "Subscribe $request")
            it.send(request)
        }

        return ids
    }

    fun subscribeByRelay(relayUrl: String, filters: List<Filter>): List<String> {
        if (filters.isEmpty() || !sockets.containsKey(relayUrl)) {
            return listOf()
        }
        val ids = mutableListOf<String>()
        sockets.entries.find { it.key == relayUrl }?.value?.let {
            val subscriptionId = UUID.randomUUID().toString()
            ids.add(subscriptionId)
            subscriptions[subscriptionId] = it
            val request = createSubscriptionRequest(subscriptionId, filters)
            Log.i(TAG, "Subscribe $request")
            it.send(request)
        }

        return ids
    }

    private fun createSubscriptionRequest(subscriptionId: String, filters: List<Filter>): String {
        return """["REQ","$subscriptionId",${filters.joinToString(",") { it.toJson() }}]"""
    }

    fun unsubscribe(subscriptionId: String) {
        Log.i(TAG, "Unsubscribe from $subscriptionId")

        val request = """["CLOSE","$subscriptionId"]"""
        subscriptions[subscriptionId]?.send(request)
        subscriptions.remove(subscriptionId)
    }

    fun publish(event: Event) {
        val request = """["EVENT",${event.toJson()}]"""
        Log.i(TAG, "Publish $request")
        sockets.values.forEach { it.send(request) }
    }

    fun addRelays(urls: List<String>) {
        urls.forEach { addRelay(it) }
    }

    private fun addRelay(url: String) {
        Log.i(TAG, "Add relay $url")

        if (sockets.containsKey(url)) {
            return
        }
        try {
            val request = Request.Builder().url(url).build()
            val socket = httpClient.newWebSocket(request = request, listener = baseListener)
            sockets[url] = socket
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to connect to $url", t)
        }
    }

    private fun removeRelay(url: String) {
        Log.i(TAG, "Remove relay $url")
        sockets[url]?.close(1000, "Normal closure")
        sockets.remove(url)
    }

    fun setListener(listener: NostrListener) {
        Log.i(TAG, "Set listener")

        nostrListener = listener
    }

    fun close() {
        Log.i(TAG, "Close connections")
        sockets.keys.forEach { removeRelay(it) }
        httpClient.dispatcher.executorService.shutdown()
    }

    private fun getRelayUrl(webSocket: WebSocket): String? {
        return sockets.entries.find { it.value == webSocket }?.key
    }

}
