package com.kaiwolfram.nozzle.data.nostr

import android.util.Log
import com.kaiwolfram.nostrclientkt.Filter

private const val TAG = "NostrSubscriber"

class NostrSubscriber(private val nostrService: INostrService) : INostrSubscriber {
    private val feedSubscriptions = mutableListOf<String>()
    private val additionalFeedDataSubscriptions = mutableListOf<String>()

    override fun subscribeToProfileMetadataAndContactList(pubkey: String): List<String> {
        Log.i(TAG, "Subscribe metadata and contact list for $pubkey")
        val profileFilter = Filter.createProfileFilter(pubkey = pubkey)
        val contactListFilter = Filter.createContactListFilter(pubkey = pubkey)

        return nostrService.subscribe(
            filters = listOf(profileFilter, contactListFilter),
            unsubOnEOSE = true
        )
    }

    override fun subscribeToFeed(contactPubkeys: List<String>, since: Long?): List<String> {
        Log.i(TAG, "Subscribe to feed of ${contactPubkeys.size} contacts")
        val limit = if (since == null) 250 else null
        val postFilter = Filter.createPostFilter(
            pubkeys = contactPubkeys,
            since = since,
            limit = limit
        )

        val ids = nostrService.subscribe(filters = listOf(postFilter))
        feedSubscriptions.addAll(ids)

        return ids
    }

    override fun subscribeToAdditionalFeedData(
        involvedPubkeys: Set<String>,
        referencedPostIds: Set<String>
    ): List<String> {
        Log.i(
            TAG,
            "Subscribe to additional feed data of ${involvedPubkeys.size} pubkeys " +
                    "and ${referencedPostIds.size} referenced posts"
        )
        if (involvedPubkeys.isEmpty() && referencedPostIds.isEmpty()) return listOf()

        val profileFilter = Filter.createProfileFilter(pubkeys = involvedPubkeys.toList())
        val postFilter = Filter.createPostFilter(ids = referencedPostIds.toList())

        val ids = nostrService.subscribe(
            filters = listOf(profileFilter, postFilter),
            unsubOnEOSE = true
        )
        additionalFeedDataSubscriptions.addAll(ids)

        return ids
    }

    override fun unsubscribeFeeds() {
        nostrService.unsubscribe(feedSubscriptions)
        feedSubscriptions.clear()
    }

    override fun unsubscribeAdditionalFeedData() {
        nostrService.unsubscribe(additionalFeedDataSubscriptions)
        additionalFeedDataSubscriptions.clear()
    }
}
