package com.kaiwolfram.nozzle.data.nostr

import com.kaiwolfram.nostrclientkt.Event

interface INostrService {
    fun publishProfile(name: String, about: String, picture: String, nip05: String): Event
    fun sendPost(content: String): Event
    fun sendRepost(postId: String, quote: String): Event
    fun sendLike(postId: String, postPubkey: String): Event
    fun sendReply(postId: String, content: String): Event
    fun updateContactList(contacts: List<String>): Event
    fun subscribeToProfileMetadataAndContactList(pubkey: String): String
}
