package com.kaiwolfram.nozzle.data.nostr

import android.util.Log
import com.kaiwolfram.nostrclientkt.model.Filter
import com.kaiwolfram.nostrclientkt.model.RelaySelection
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.utils.getCurrentTimeInSeconds
import com.kaiwolfram.nozzle.data.utils.listReferencedPostIds
import com.kaiwolfram.nozzle.data.utils.listReferencedPubkeys
import com.kaiwolfram.nozzle.model.PostWithMeta

private const val TAG = "NostrSubscriber"

class NostrSubscriber(
    private val nostrService: INostrService,
    private val postDao: PostDao
) : INostrSubscriber {
    private val feedSubscriptions = mutableListOf<String>()
    private val threadSubscriptions = mutableListOf<String>()
    private val additionalFeedDataSubscriptions = mutableListOf<String>()
    private val profileSubscriptions = mutableListOf<String>()

    override fun subscribeToProfileMetadataAndContactList(pubkeys: List<String>): List<String> {
        Log.i(TAG, "Subscribe metadata and contact list for ${pubkeys.size} pubkeys")
        val profileFilter = Filter.createProfileFilter(pubkeys = pubkeys)
        val contactListFilter = Filter.createContactListFilter(pubkeys = pubkeys)

        val ids = nostrService.subscribe(
            filters = listOf(profileFilter, contactListFilter),
            unsubOnEOSE = true
        )
        profileSubscriptions.addAll(ids)

        return ids
    }

    override fun subscribeToFeed(
        authorPubkeys: List<String>,
        limit: Int,
        until: Long?,
        relaySelection: RelaySelection,
    ): List<String> {
        Log.i(TAG, "Subscribe to feed with of ${authorPubkeys.size} authors")
        val postFilter = Filter.createPostFilter(
            pubkeys = authorPubkeys,
            until = until ?: getCurrentTimeInSeconds(),
            limit = limit
        )
        val ids = nostrService.subscribe(
            filters = listOf(postFilter),
            unsubOnEOSE = until != null,
            relaySelection = relaySelection,
        )
        feedSubscriptions.addAll(ids)

        return ids
    }

    override suspend fun subscribeToAdditionalPostsData(
        posts: List<PostWithMeta>
    ): List<String> {
        Log.i(TAG, "Subscribe to additional posts data")
        if (posts.isEmpty()) return listOf()

        val postIds = posts.map { it.id }
        val referencedPostIds = listReferencedPostIds(posts)
        val referencedPubkeys = mutableSetOf<String>()
        referencedPubkeys.addAll(listReferencedPubkeys(posts))
        referencedPubkeys.addAll(postDao.listAuthorPubkeys(referencedPostIds))

        val filters = mutableListOf<Filter>()
        filters.add(Filter.createReactionFilter(e = postIds))
        filters.add(Filter.createPostFilter(e = postIds))
        if (referencedPostIds.isNotEmpty()) {
            filters.add(Filter.createPostFilter(ids = referencedPostIds))
        }
        if (referencedPubkeys.isNotEmpty()) {
            filters.add(Filter.createProfileFilter(pubkeys = referencedPubkeys.toList()))
        }

        val ids = nostrService.subscribe(
            filters = filters,
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

        val postIds = mutableListOf(currentPostId)
        replyToId?.let { postIds.add(it) }
        replyToRootId?.let { postIds.add(it) }

        val filters = mutableListOf<Filter>()
        filters.add(Filter.createPostFilter(e = postIds))
        filters.add(Filter.createPostFilter(ids = postIds))

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
