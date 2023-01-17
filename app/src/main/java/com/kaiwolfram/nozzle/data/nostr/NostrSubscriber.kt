package com.kaiwolfram.nozzle.data.nostr

import android.util.Log
import com.kaiwolfram.nostrclientkt.Filter
import com.kaiwolfram.nozzle.data.utils.TimeConstants
import com.kaiwolfram.nozzle.data.utils.getCurrentTimeInSeconds

private const val TAG = "NostrSubscriber"

class NostrSubscriber(private val nostrService: INostrService) : INostrSubscriber {
    private val feedSubscriptions = mutableListOf<String>()
    private val threadSubscriptions = mutableListOf<String>()
    private val additionalFeedDataSubscriptions = mutableListOf<String>()
    private val profileSubscriptions = mutableListOf<String>()

    override fun subscribeToProfileMetadataAndContactList(pubkey: String): List<String> {
        Log.i(TAG, "Subscribe metadata and contact list for $pubkey")
        val profileFilter = Filter.createProfileFilter(pubkey = pubkey)
        val contactListFilter = Filter.createContactListFilter(pubkey = pubkey)

        return nostrService.subscribe(
            filters = listOf(profileFilter, contactListFilter),
            unsubOnEOSE = true
        )
    }

    private fun getCurrentTimePlus5min() =
        getCurrentTimeInSeconds() + (5 * TimeConstants.MINUTE_IN_SECONDS)

    override fun subscribeToFeed(
        authorPubkeys: List<String>,
        limit: Int,
        until: Long?,
    ): List<String> {
        Log.i(TAG, "Subscribe to feed of ${authorPubkeys.size} contacts")
        val postFilter = Filter.createPostFilter(
            pubkeys = authorPubkeys,
            until = until ?: getCurrentTimePlus5min(),
            limit = limit
        )

        val ids = nostrService.subscribe(filters = listOf(postFilter))
        feedSubscriptions.addAll(ids)

        return ids
    }

    override fun subscribeToAdditionalPostsData(
        postIds: List<String>,
        referencedPubkeys: List<String>,
        referencedPostIds: List<String>
    ): List<String> {
        Log.i(TAG, "Subscribe to additional posts data")
        if (referencedPubkeys.isEmpty() && referencedPostIds.isEmpty()) return listOf()

        val reactionFilter = Filter.createReactionFilter(e = postIds)
        val replyFilter = Filter.createReplyFilter(e = postIds)
        val profileFilter = Filter.createProfileFilter(pubkeys = referencedPubkeys)
        val postFilter = Filter.createPostFilter(ids = referencedPostIds)

        val ids = nostrService.subscribe(
            filters = listOf(profileFilter, postFilter, reactionFilter, replyFilter),
            unsubOnEOSE = true
        )
        additionalFeedDataSubscriptions.addAll(ids)

        return ids
    }

    override fun subscribeToThread(
        currentPostId: String,
        replyToId: String?,
        replyToRootId: String?
    ): List<String> {
        Log.i(TAG, "Subscribe to thread")

        val replyFilter = Filter.createReplyFilter(e = listOf(currentPostId))

        val filters = mutableListOf(replyFilter)
        replyToId?.let { filters.add(Filter.createPostFilter(ids = listOf(it))) }
        replyToRootId?.let { filters.add(Filter.createPostFilter(ids = listOf(it))) }

        val ids = nostrService.subscribe(
            filters = filters,
            unsubOnEOSE = true
        )
        threadSubscriptions.addAll(ids)

        return ids
    }

    override fun subscribeToProfiles(pubkeys: List<String>): List<String> {
        Log.i(TAG, "Subscribe to ${pubkeys.size} profiles")

        if (pubkeys.isEmpty()) return listOf()

        val profileFilter = Filter.createProfileFilter(pubkeys = pubkeys)

        val ids = nostrService.subscribe(
            filters = listOf(profileFilter),
            unsubOnEOSE = true
        )
        profileSubscriptions.addAll(ids)

        return ids
    }

    override fun unsubscribeFeeds() {
        nostrService.unsubscribe(feedSubscriptions)
        feedSubscriptions.clear()
    }

    override fun unsubscribeAdditionalPostsData() {
        nostrService.unsubscribe(additionalFeedDataSubscriptions)
        additionalFeedDataSubscriptions.clear()
    }

    override fun unsubscribeThread() {
        nostrService.unsubscribe(threadSubscriptions)
        threadSubscriptions.clear()
    }

    override fun unsubscribeProfiles() {
        nostrService.unsubscribe(profileSubscriptions)
        profileSubscriptions.clear()
    }
}
