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
import com.kaiwolfram.nozzle.data.provider.IRelayProvider
import com.kaiwolfram.nozzle.model.AllRelays
import com.kaiwolfram.nozzle.model.MultipleRelays
import com.kaiwolfram.nozzle.model.RelaySelection
import java.util.*

private const val TAG = "NostrService"

class NostrService(
    keyManager: IKeyManager,
    private val relayProvider: IRelayProvider,
    private val eventProcessor: IEventProcessor
) : INostrService {
    private val keys: Keys = keyManager.getKeys()
    private val client = Client()
    private val unsubOnEOSECache = Collections.synchronizedSet(mutableSetOf<String>())
    private val listener = object : NostrListener {
        override fun onOpen(msg: String) {
            Log.i(TAG, "OnOpen: $msg")
        }

        override fun onEvent(subscriptionId: String, event: Event, relayUrl: String?) {
            Log.d(
                TAG,
                "OnEvent: id ${event.id}, kind ${event.kind}, relay $relayUrl in subscription $subscriptionId"
            )
            eventProcessor.process(event = event, relayUrl = relayUrl)
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
        val relays = relayProvider.listRelays()
        Log.i(TAG, "Add ${relays.size} relays")
        client.addRelays(relays)
    }

    override fun publishProfile(metadata: Metadata): Event {
        Log.i(TAG, "Publish profile $metadata")
        val event = Event.createMetadataEvent(
            metadata = metadata,
            keys = keys,
        )
        Log.i(TAG, "new profile is valid ${event.verify()}")
        client.publishToAllRelays(event)

        return event
    }

    override fun sendPost(content: String, relaySelection: RelaySelection): Event {
        Log.i(TAG, "Send post '${content.take(50)}'")
        val event = Event.createTextNoteEvent(
            post = Post(msg = content),
            keys = keys,
        )
        when (relaySelection) {
            is AllRelays -> client.publishToAllRelays(event)
            is MultipleRelays -> client.publishToRelays(
                event = event,
                relays = relaySelection.relays
            )
        }

        return event
    }

    override fun sendRepost(postId: String, quote: String): Event {
        Log.i(TAG, "Send repost of $postId")
        val event = Event.createTextNoteEvent(
            post = Post(
                repostId = RepostId(
                    repostId = postId,
                    relayUrl = relayProvider.listRelays().firstOrNull().orEmpty()
                ),
                msg = quote
            ),
            keys = keys,
        )
        client.publishToAllRelays(event)

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
        client.publishToAllRelays(event)

        return event
    }

    override fun sendReply(
        replyTo: ReplyTo,
        content: String,
        relaySelection: RelaySelection
    ): Event {
        Log.i(TAG, "Send reply to ${replyTo.replyTo} of root ${replyTo.replyToRoot}")
        val event = Event.createTextNoteEvent(
            post = Post(replyTo = replyTo, msg = content),
            keys = keys,
        )
        when (relaySelection) {
            is AllRelays -> client.publishToAllRelays(event)
            is MultipleRelays -> client.publishToRelays(
                event = event,
                relays = relaySelection.relays
            )
        }

        return event
    }

    override fun updateContactList(contacts: List<ContactListEntry>): Event {
        Log.i(TAG, "Update contact list with ${contacts.size} contacts")
        val event = Event.createContactListEvent(
            contacts = contacts,
            keys = keys,
        )
        client.publishToAllRelays(event)

        return event
    }

    override fun subscribe(filters: List<Filter>, unsubOnEOSE: Boolean): List<String> {
        val subscriptionIds = client.subscribe(filters)
        if (subscriptionIds.isNotEmpty() && unsubOnEOSE) {
            unsubOnEOSECache.addAll(subscriptionIds)
        }

        return subscriptionIds
    }

    override fun subscribeByRelay(
        relayUrl: String,
        filters: List<Filter>,
        unsubOnEOSE: Boolean
    ): List<String> {
        val subscriptionIds = client.subscribeByRelay(relayUrl = relayUrl, filters = filters)
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
