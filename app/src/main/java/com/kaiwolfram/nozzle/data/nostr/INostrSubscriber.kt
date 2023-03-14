package com.kaiwolfram.nozzle.data.nostr

import com.kaiwolfram.nozzle.model.PostWithMeta

interface INostrSubscriber {
    fun subscribeToProfileMetadataAndContactList(
        pubkeys: Collection<String>,
        relays: Collection<String>? = null
    ): List<String>

    fun subscribeToFeed(
        authorPubkeys: Collection<String>?,
        limit: Int,
        until: Long?,
        relays: Collection<String>? = null,
    ): List<String>

    suspend fun subscribeToAdditionalPostsData(
        posts: Collection<PostWithMeta>,
        relays: Collection<String>? = null
    ): List<String>

    fun subscribeToThread(
        currentPostId: String,
        replyToId: String? = null,
        replyToRootId: String? = null,
        relays: Collection<String>? = null,
    ): List<String>

    fun subscribeToProfiles(
        pubkeys: Collection<String>,
        relays: Collection<String>? = null
    ): List<String>

    fun subscribeToNip65(pubkeys: Collection<String>): List<String>

    fun unsubscribeFeeds()

    fun unsubscribeAdditionalPostsData()

    fun unsubscribeThread()

    fun unsubscribeProfiles()

    fun unsubscribeToNip65()
}
