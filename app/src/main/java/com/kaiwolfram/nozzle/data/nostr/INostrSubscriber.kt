package com.kaiwolfram.nozzle.data.nostr

interface INostrSubscriber {
    fun subscribeToProfileMetadataAndContactList(pubkey: String): List<String>

    fun subscribeToFeed(
        authorPubkeys: List<String>,
        limit: Int,
        until: Long? = null,
    ): List<String>

    fun subscribeToAdditionalPostsData(
        postIds: List<String>,
        referencedPubkeys: List<String>,
        referencedPostIds: List<String>
    ): List<String>

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
