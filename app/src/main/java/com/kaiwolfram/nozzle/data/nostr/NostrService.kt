package com.kaiwolfram.nozzle.data.nostr

import android.util.Log
import com.kaiwolfram.nostrclientkt.*
import com.kaiwolfram.nostrclientkt.model.Event
import com.kaiwolfram.nostrclientkt.model.Filter
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nostrclientkt.net.Client
import com.kaiwolfram.nostrclientkt.net.NostrListener
import com.kaiwolfram.nozzle.data.eventProcessor.IEventProcessor
import com.kaiwolfram.nozzle.data.manager.IKeyManager
import java.util.*

private const val TAG = "NostrService"

class NostrService(
    keyManager: IKeyManager,
    private val eventProcessor: IEventProcessor
) : INostrService {
    private val keys: Keys = keyManager.getKeys()
    private val client = Client()
    private val relays = listOf(
        "wss://nostr-pub.wellorder.net",
        "wss://nostr.onsats.org",
        "wss://nostr-relay.wlvs.space",
        "wss://nostr.bitcoiner.social",
        "wss://relay.damus.io",
        "wss://nostr.zebedee.cloud",
        "wss://nostr.fmt.wiz.biz",
        "wss://nostr.walletofsatoshi.com",
    )
    private val unsubOnEOSECache = Collections.synchronizedSet(mutableSetOf<String>())
    private val listener = object : NostrListener {
        override fun onOpen(msg: String) {
            Log.i(TAG, "OnOpen: $msg")
        }

        override fun onEvent(subscriptionId: String, event: Event) {
            Log.d(
                TAG,
                "OnEvent: id ${event.id}, kind ${event.kind} in subscription $subscriptionId"
            )
            eventProcessor.process(event)
        }

        override fun onError(msg: String, throwable: Throwable?) {
            Log.w(TAG, "OnError: $msg", throwable)
        }

        override fun onEOSE(subscriptionId: String) {
            Log.i(TAG, "OnEOSE: $subscriptionId")
            if (unsubOnEOSECache.remove(subscriptionId)) {
                Log.i(TAG, "Unsubscribe onEOSE $subscriptionId")
                client.unsubscribe(subscriptionId)
            }
        }

        override fun onClose(reason: String) {
            Log.i(TAG, "OnClose: $reason")
        }

        override fun onFailure(msg: String?, throwable: Throwable?) {
            Log.w(TAG, "OnFailure: $msg", throwable)
        }

        override fun onOk(id: String) {
            Log.d(TAG, "OnOk: $id")
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
        Log.i(TAG, "new profile is valid ${event.verify()}")
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

    override fun subscribe(filters: List<Filter>, unsubOnEOSE: Boolean): List<String> {
        val subscriptionIds = client.subscribe(filters)
        if (subscriptionIds.isNotEmpty() && unsubOnEOSE) {
            unsubOnEOSECache.addAll(subscriptionIds)
        }

        return subscriptionIds
    }

    override fun unsubscribe(subscriptionIds: List<String>) {
        if (subscriptionIds.isNotEmpty()) {
            subscriptionIds.forEach {
                client.unsubscribe(it)
            }
        }
    }

    override fun close() {
        Log.i(TAG, "Close connections")
        client.close()
    }
}
