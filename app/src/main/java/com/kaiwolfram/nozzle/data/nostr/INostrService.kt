package com.kaiwolfram.nozzle.data.nostr

import com.kaiwolfram.nozzle.data.room.entity.EventEntity

interface INostrService {
    fun getFollowerCount(pubkey: String): Int
    fun getFollowingCount(pubkey: String): Int
    fun getProfile(pubkey: String): NostrProfile?
    fun listPosts(pubkey: String): List<EventEntity>
    fun listFollowedProfiles(pubKey: String): List<NostrProfile>
    fun listPosts(): List<EventEntity>
    fun getPost(postId: String): EventEntity
    fun likePost(postId: String)
    fun repost(postId: String)
    fun follow(pubkey: String)
    fun unfollow(pubkey: String)
    fun send(recipientPubkey: String, reply: String)
}
