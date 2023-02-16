package com.kaiwolfram.nozzle.data.nostr

import com.kaiwolfram.nostrclientkt.model.RelaySelection
import com.kaiwolfram.nozzle.model.PostWithMeta

interface INostrSubscriber {
    fun subscribeToProfileMetadataAndContactList(pubkeys: List<String>): List<String>

    fun subscribeToFeed(
        authorPubkeys: List<String>,
        limit: Int,
        until: Long?,
        relaySelection: RelaySelection,
    ): List<String>

    suspend fun subscribeToAdditionalPostsData(posts: List<PostWithMeta>): List<String>

    fun subscribeToThread(
        currentPostId: String,
        replyToId: String? = null,
        replyToRootId: String? = null
    ): List<String>

    fun subscribeToProfiles(pubkeys: List<String>): List<String>

    fun unsubscribeFeeds()

    fun unsubscribeAdditionalPostsData()

    fun unsubscribeThread()

    fun unsubscribeProfiles()
}
