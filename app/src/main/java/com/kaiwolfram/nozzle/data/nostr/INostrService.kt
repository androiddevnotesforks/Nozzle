package com.kaiwolfram.nozzle.data.nostr

import com.kaiwolfram.nostrclientkt.ContactListEntry
import com.kaiwolfram.nostrclientkt.ReplyTo
import com.kaiwolfram.nostrclientkt.model.Event
import com.kaiwolfram.nostrclientkt.model.Filter
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nozzle.model.AllRelays
import com.kaiwolfram.nozzle.model.RelaySelection

interface INostrService {
    fun publishProfile(metadata: Metadata): Event
    fun sendPost(content: String, relaySelection: RelaySelection = AllRelays): Event
    fun sendRepost(postId: String, quote: String): Event
    fun sendLike(postId: String, postPubkey: String): Event
    fun sendReply(
        replyTo: ReplyTo,
        content: String,
        relaySelection: RelaySelection = AllRelays
    ): Event

    fun updateContactList(contacts: List<ContactListEntry>): Event
    fun subscribe(filters: List<Filter>, unsubOnEOSE: Boolean = false): List<String>
    fun subscribeByRelay(
        relayUrl: String,
        filters: List<Filter>,
        unsubOnEOSE: Boolean = false
    ): List<String>

    fun unsubscribe(subscriptionIds: List<String>)
    fun close()
}
