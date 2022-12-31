package com.kaiwolfram.nozzle.data.nostr

import android.util.Log
import com.kaiwolfram.nostrclientkt.*
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
            // TODO: Save in db if new or more recent
            TODO()
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

        override fun onFailure(msg: String?) {
            Log.i(TAG, "Relay failure error: $msg")
        }

    }

    init {
        client.register(listener)
        client.addRelays(relays)
    }

    override fun publishProfile(
        name: String,
        about: String,
        picture: String,
        nip05: String
    ): Event {
        Log.i(TAG, "Publish profile")
        val event = Event.createMetadataEvent(
            metadata = Metadata(name, about, picture, nip05),
            keys = keys,
        )
        client.publish(event)

        return event
    }

    override fun sendPost(content: String): Event {
        Log.i(TAG, "Send post '${content.take(50)}'")
        val event = Event.createTextNoteEvent(
            post = Post(msg = content),
            keys = keys,
        )
        client.publish(event)

        return event
    }

    override fun sendRepost(postId: String, quote: String): Event {
        Log.i(TAG, "Send repost of $postId")
        val event = Event.createTextNoteEvent(
            post = Post(
                // TODO: real relay
                repostId = RepostId(repostId = postId, relayUrl = relays.first()),
                msg = quote
            ),
            keys = keys,
        )
        client.publish(event)

        return event
    }

    override fun sendLike(postId: String, postPubkey: String): Event {
        Log.i(TAG, "Send like reaction to $postId")
        val event = Event.createReactionEvent(
            eventId = postId,
            eventPubkey = postPubkey,
            isPositive = true,
            keys = keys,
        )
        client.publish(event)

        return event
    }

    override fun sendReply(postId: String, content: String): Event {
        Log.i(TAG, "Send reply to $postId")
        // TODO: Set p tag to notify OG Post author?
        val event = Event.createTextNoteEvent(
            post = Post(
                // TODO: real relay
                replyTo = ReplyTo(replyTo = postId, relayUrl = relays.first()),
                msg = content,
            ),
            keys = keys,
        )
        client.publish(event)

        return event
    }

    override fun updateContactList(contacts: List<String>): Event {
        Log.i(TAG, "Update contact list with ${contacts.size} contacts")
        val event = Event.createContactListEvent(
            contacts = contacts,
            keys = keys,
        )
        client.publish(event)

        return event
    }

    override fun subscribeToProfileMetadataAndContactList(pubkey: String): String {
        Log.i(TAG, "Subscribe metadata and contact list for $pubkey")
        val profileFilter = Filter.createProfileFilter(pubkey = pubkey)
        val contactListFilter = Filter.createContactListFilter(pubkey = pubkey)

        return client.subscribe(filters = listOf(profileFilter, contactListFilter))
    }

}
