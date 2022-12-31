package com.kaiwolfram.nozzle.data.nostr

import com.kaiwolfram.nostrclientkt.Event
import com.kaiwolfram.nostrclientkt.Metadata
import com.kaiwolfram.nostrclientkt.ReplyTo

interface INostrService {
    fun publishProfile(metadata: Metadata): Event
    fun sendPost(content: String): Event
    fun sendRepost(postId: String, quote: String): Event
    fun sendLike(postId: String, postPubkey: String): Event
    fun sendReply(replyTo: ReplyTo, content: String): Event
    fun updateContactList(contacts: List<String>): Event
    fun subscribeToProfileMetadataAndContactList(pubkey: String): String
}
