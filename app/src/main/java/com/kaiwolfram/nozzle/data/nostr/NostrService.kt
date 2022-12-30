package com.kaiwolfram.nozzle.data.nostr

import android.util.Log
import com.kaiwolfram.nostrclientkt.Event
import com.kaiwolfram.nostrclientkt.Keys
import com.kaiwolfram.nostrclientkt.Metadata
import com.kaiwolfram.nostrclientkt.net.Client
import com.kaiwolfram.nostrclientkt.net.NostrListener
import com.kaiwolfram.nozzle.data.preferences.key.IKeyManager

private const val TAG = "NostrService"

class NostrService(keyManager: IKeyManager) : INostrService {
    private val keys: Keys = keyManager.getKeys()
    private val client = Client()
    private val relays = listOf(
        "wss://nostr-2.zebedee.cloud",
        "wss://relay.damus.io",
        "wss://nostr.einundzwanzig.space"
    )
    private val listener = object : NostrListener {
        override fun onOpen(msg: String) {
            Log.i(TAG, "Relay is ready: $msg")
        }

        override fun onEvent(subscriptionId: String, event: Event) {
            Log.i(TAG, "Received event ${event.id} in subscription $subscriptionId")
        }

        override fun onError(msg: String) {
            Log.i(TAG, "Relay error: $msg")
        }

        override fun onEOSE(subscriptionId: String) {
            Log.i(TAG, "EOSE on subscription $subscriptionId")
        }

        override fun onClose(reason: String) {
            Log.i(TAG, "Closed relay connection: $reason")
        }

        override fun onFailure(message: String?) {
            Log.i(TAG, "Relay failure error: $message")
        }

    }

    init {
        client.register(listener)
        client.addRelays(relays)
    }

    override fun publishProfile(name: String, about: String, picture: String, nip05: String) {
        val event = Event.createMetadataEvent(
            metadata = Metadata(name, about, picture, nip05),
            keys = keys
        )
        client.publish(event)
    }
}
