package com.kaiwolfram.nozzle.data.nostr

interface INostrService {
    fun publishProfile(name: String, about: String, picture: String, nip05: String)
    fun sendPost(content: String)
    fun sendRepost(postId: String, quote: String)
    fun sendLike(postId: String)
    fun sendReply(recipientPubkey: String, content: String)
    fun subscribeToProfileMetadata(pubkey: String)
    fun follow(pubkey: String)
    fun unfollow(pubkey: String)
}
