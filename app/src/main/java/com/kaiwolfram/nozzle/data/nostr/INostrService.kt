package com.kaiwolfram.nozzle.data.nostr

import com.kaiwolfram.nostrclientkt.ContactListEntry
import com.kaiwolfram.nostrclientkt.ReplyTo
import com.kaiwolfram.nostrclientkt.model.*

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

    fun subscribe(
        filters: List<Filter>,
        unsubOnEOSE: Boolean = false,
        relaySelection: RelaySelection = AllRelays
    ): List<String>

    fun unsubscribe(subscriptionIds: List<String>)

    fun close()
}
