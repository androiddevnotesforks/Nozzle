package com.kaiwolfram.nozzle.data.nostr

import com.kaiwolfram.nostrclientkt.model.*

interface INostrService {
    fun publishProfile(metadata: Metadata): Event

    fun sendPost(content: String, relays: Collection<String>?): Event

    fun sendRepost(
        postId: String,
        postPubkey: String,
        quote: String,
        originUrl: String,
        relays: Collection<String>?
    ): Event

    fun sendLike(postId: String, postPubkey: String, relays: Collection<String>?): Event

    fun sendReply(
        replyTo: ReplyTo,
        content: String,
        relays: Collection<String>?
    ): Event

    fun updateContactList(contacts: List<ContactListEntry>): Event

    fun subscribe(
        filters: List<Filter>,
        unsubOnEOSE: Boolean,
        relays: Collection<String>?,
    ): List<String>

    fun unsubscribe(subscriptionIds: List<String>)

    fun close()
}
