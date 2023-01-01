package com.kaiwolfram.nozzle.data.nostr

import com.kaiwolfram.nostrclientkt.ContactListEntry
import com.kaiwolfram.nostrclientkt.Event
import com.kaiwolfram.nostrclientkt.Metadata
import com.kaiwolfram.nostrclientkt.ReplyTo

interface INostrService {
    fun publishProfile(metadata: Metadata): Event
    fun sendPost(content: String): Event
    fun sendRepost(postId: String, quote: String): Event
    fun sendLike(postId: String, postPubkey: String): Event
    fun sendReply(replyTo: ReplyTo, content: String): Event
    fun updateContactList(contacts: List<ContactListEntry>): Event
    fun subscribeToProfileMetadataAndContactList(pubkey: String): String
    fun subscribeToFeed(contactPubkeys: List<String>, since: Long? = null): String
    fun close()
}
