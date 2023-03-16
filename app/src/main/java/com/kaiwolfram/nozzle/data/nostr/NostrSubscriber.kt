package com.kaiwolfram.nozzle.data.nostr

import android.util.Log
import com.kaiwolfram.nostrclientkt.model.Filter
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.utils.getCurrentTimeInSeconds
import com.kaiwolfram.nozzle.data.utils.getIdsPerRelayHintMap
import com.kaiwolfram.nozzle.data.utils.listReferencedPostIds
import com.kaiwolfram.nozzle.data.utils.listReferencedPubkeys
import com.kaiwolfram.nozzle.model.PostWithMeta
import java.util.*

private const val TAG = "NostrSubscriber"

// TODO: Check if separation of sub and unsub is needed or should be combined for less code
class NostrSubscriber(
    private val nostrService: INostrService,
    private val postDao: PostDao
) : INostrSubscriber {
    private val feedSubscriptions = Collections.synchronizedList(mutableListOf<String>())
    private val threadSubscriptions = Collections.synchronizedList(mutableListOf<String>())
    private val additionalFeedDataSubscriptions =
        Collections.synchronizedList(mutableListOf<String>())
    private val profileSubscriptions = Collections.synchronizedList(mutableListOf<String>())
    private val nip65Subscriptions = Collections.synchronizedList(mutableListOf<String>())

    override fun subscribeToProfileMetadataAndContactList(
        pubkeys: Collection<String>,
        relays: Collection<String>?
    ): List<String> {
        Log.i(TAG, "Subscribe metadata and contact list for ${pubkeys.size} pubkeys")
        val profileFilter = Filter.createProfileFilter(pubkeys = pubkeys.toList())
        val contactListFilter = Filter.createContactListFilter(pubkeys = pubkeys.toList())

        val ids = nostrService.subscribe(
            filters = listOf(profileFilter, contactListFilter),
            unsubOnEOSE = true,
            relays = relays
        )
        profileSubscriptions.addAll(ids)

        return ids
    }

    override fun subscribeToFeed(
        authorPubkeys: Collection<String>?,
        limit: Int,
        until: Long?,
        relays: Collection<String>?
    ): List<String> {
        Log.i(TAG, "Subscribe to feed of ${authorPubkeys?.size} pubkeys in ${relays?.size} relays")
        val postFilter = Filter.createPostFilter(
            pubkeys = authorPubkeys,
            until = until ?: getCurrentTimeInSeconds(),
            limit = limit
        )
        val ids = nostrService.subscribe(
            filters = listOf(postFilter),
            unsubOnEOSE = true,
            relays = relays,
        )
        feedSubscriptions.addAll(ids)

        return ids
    }

    // TODO: Set limit. Large threads will fry your device otherwise
    override suspend fun subscribeToAdditionalPostsData(
        posts: Collection<PostWithMeta>,
        relays: Collection<String>?,
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
            unsubOnEOSE = true,
            relays = relays,
        ).toMutableList()
        ids.addAll(subscribeToReplyRelayHint(posts, relays))
        additionalFeedDataSubscriptions.addAll(ids)

        return ids
    }

    private fun subscribeToReplyRelayHint(
        posts: Collection<PostWithMeta>,
        relays: Collection<String>?,
    ): List<String> {
        val ids = mutableListOf<String>()
        getIdsPerRelayHintMap(posts = posts).forEach { (relayHint, hintedPostIds) ->
            // TODO: Relay URL utils for checking this
            if (relays?.contains(relayHint) == false
                && relayHint.startsWith("wss://")
                && hintedPostIds.isNotEmpty()
            ) {
                Log.i(
                    TAG,
                    "Subscribe to reply relay hint of ${hintedPostIds.size} posts in $relayHint"
                )
                val relayHintIds = nostrService.subscribe(
                    filters = listOf(Filter.createPostFilter(ids = hintedPostIds)),
                    unsubOnEOSE = true,
                    relays = listOf(relayHint),
                )
                ids.addAll(relayHintIds)
            }
        }

        return ids
    }

    override fun subscribeToThread(
        currentPostId: String,
        replyToId: String?,
        replyToRootId: String?,
        relays: Collection<String>?,
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
            unsubOnEOSE = true,
            relays = relays,
        )
        threadSubscriptions.addAll(ids)

        return ids
    }

    override fun subscribeToProfiles(
        pubkeys: Collection<String>,
        relays: Collection<String>?,
    ): List<String> {
        Log.i(TAG, "Subscribe to ${pubkeys.size} profiles")

        if (pubkeys.isEmpty()) return listOf()

        val profileFilter = Filter.createProfileFilter(pubkeys = pubkeys.toList())

        val ids = nostrService.subscribe(
            filters = listOf(profileFilter),
            unsubOnEOSE = true,
            relays = relays
        )
        profileSubscriptions.addAll(ids)

        return ids
    }

    // No relaySelection needed because nip65 could be anywhere
    override fun subscribeToNip65(pubkeys: Collection<String>): List<String> {
        Log.i(TAG, "Subscribe to ${pubkeys.size} nip65s")

        if (pubkeys.isEmpty()) return listOf()

        val nip65Filter = Filter.createNip65Filter(pubkeys = pubkeys.toList())

        val ids = nostrService.subscribe(
            filters = listOf(nip65Filter),
            unsubOnEOSE = true,
            relays = null
        )
        nip65Subscriptions.addAll(ids)

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

    override fun unsubscribeToNip65() {
        nostrService.unsubscribe(nip65Subscriptions)
        nip65Subscriptions.clear()
    }
}
