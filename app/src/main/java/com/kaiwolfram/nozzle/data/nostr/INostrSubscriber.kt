package com.kaiwolfram.nozzle.data.nostr

interface INostrSubscriber {
    fun subscribeToProfileMetadataAndContactList(pubkey: String): List<String>

    fun subscribeToFeed(contactPubkeys: List<String>, until: Long?, limit: Int): List<String>

    fun subscribeToAdditionalPostsData(
        postIds: List<String>,
        involvedPubkeys: List<String>,
        referencedPostIds: List<String>
    ): List<String>

    fun subscribeToThread(
        currentPostId: String,
        replyToId: String? = null,
        replyToRootId: String? = null
    ): List<String>

    fun unsubscribeFeeds()

    fun unsubscribeAdditionalPostsData()

    fun unsubscribeToThread()
}
