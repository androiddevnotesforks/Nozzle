package com.kaiwolfram.nozzle.data.nostr

import android.util.Log
import com.kaiwolfram.nostrclientkt.*
import com.kaiwolfram.nostrclientkt.net.Client
import com.kaiwolfram.nostrclientkt.net.NostrListener
import com.kaiwolfram.nozzle.data.defaultPubkeys
import com.kaiwolfram.nozzle.data.eventProcessor.IEventProcessor
import com.kaiwolfram.nozzle.data.manager.IKeyManager

private const val TAG = "NostrService"

class NostrService(
    keyManager: IKeyManager,
    private val eventProcessor: IEventProcessor
) : INostrService {
    private val keys: Keys = keyManager.getKeys()
    private val client = Client()
    private val relays = listOf(
        "wss://nostr.fmt.wiz.biz",
        "wss://nostr.einundzwanzig.space",
    )
    private val listener = object : NostrListener {
        override fun onOpen(msg: String) {
            Log.i(TAG, "Relay is ready: $msg")
        }

        override fun onEvent(subscriptionId: String, event: Event) {
            Log.i(TAG, "Received event $event in subscription $subscriptionId")
            eventProcessor.process(event)
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
        client.setListener(listener)
        client.addRelays(relays)
    }

    override fun publishProfile(metadata: Metadata): Event {
        Log.i(TAG, "Publish profile $metadata")
        val event = Event.createMetadataEvent(
            metadata = metadata,
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

    override fun sendReply(replyTo: ReplyTo, content: String): Event {
        Log.i(TAG, "Send reply to ${replyTo.replyTo} of root ${replyTo.replyToRoot}")
        val event = Event.createTextNoteEvent(
            post = Post(replyTo = replyTo, msg = content),
            keys = keys,
        )
        client.publish(event)

        return event
    }

    override fun updateContactList(contacts: List<ContactListEntry>): Event {
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

    override fun subscribeToFeed(contactPubkeys: List<String>, since: Long?): String {
        Log.i(TAG, "Subscribe to feed of ${contactPubkeys.size} contacts")
        val pubkeys = contactPubkeys.ifEmpty { defaultPubkeys }
        val limit = if (since == null) 150 else null
        val postFilter = Filter.createPostFilter(pubkeys = pubkeys, since = since, limit = limit)

        return client.subscribe(filters = listOf(postFilter))
    }

    // TODO: Call on end of lifecycle
    override fun close() {
        Log.i(TAG, "Close connections")
        client.close()
    }
}
