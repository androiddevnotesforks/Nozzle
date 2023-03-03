package com.kaiwolfram.nozzle.data.provider.impl

import android.util.Log
import com.kaiwolfram.nostrclientkt.model.*
import com.kaiwolfram.nozzle.data.mapper.IPostMapper
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import com.kaiwolfram.nozzle.data.provider.IContactListProvider
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.data.utils.getCurrentTimeInSeconds
import com.kaiwolfram.nozzle.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val TAG = "FeedProvider"

class FeedProvider(
    private val postMapper: IPostMapper,
    private val nostrSubscriber: INostrSubscriber,
    private val postDao: PostDao,
    private val contactListProvider: IContactListProvider,
) : IFeedProvider {

    override suspend fun getFeedFlow(
        feedSettings: FeedSettings,
        limit: Int,
        until: Long?,
        waitForSubscription: Long?,
    ): Flow<List<PostWithMeta>> {
        Log.i(TAG, "Get feed")

        nostrSubscriber.unsubscribeFeeds()
        nostrSubscriber.unsubscribeAdditionalPostsData()
        val authorPubkeys = listPubkeys(authorSelection = feedSettings.authorSelection)
        nostrSubscriber.subscribeToFeed(
            authorPubkeys = authorPubkeys,
            limit = 2 * limit,
            until = until,
            relaySelection = feedSettings.relaySelection
        )
        val relays = listRelays(relaySelection = feedSettings.relaySelection)

        waitForSubscription?.let { delay(it) }

        val posts = listPosts(
            isPosts = feedSettings.isPosts,
            isReplies = feedSettings.isReplies,
            authorPubkeys = authorPubkeys,
            relays = relays,
            until = until ?: getCurrentTimeInSeconds(),
            limit = limit,
        )

        return if (posts.isEmpty()) flow { emit(listOf()) }
        else postMapper.mapToPostsWithMetaFlow(posts)
    }

    private fun listPubkeys(authorSelection: AuthorSelection): List<String>? {
        return when (authorSelection) {
            is Everyone -> null
            is Contacts -> contactListProvider.listPersonalContactPubkeys() // TODO: Use contactListProvider
            is SingleAuthor -> listOf(authorSelection.pubkey)
        }
    }

    private fun listRelays(relaySelection: RelaySelection): List<String>? {
        return when (relaySelection) {
            is AllRelays -> null
            is Autopilot -> null // TODO: Use autopilot provider
            is PersonalNip65 -> null // TODO: Use your relays
            is MultipleRelays -> relaySelection.relays
        }
    }

    private suspend fun listPosts(
        isPosts: Boolean,
        isReplies: Boolean,
        authorPubkeys: List<String>?,
        relays: List<String>?,
        until: Long,
        limit: Int,
    ): List<PostEntity> {
        if (!isPosts && !isReplies) return listOf()

        return if (authorPubkeys == null && relays == null) {
            Log.d(TAG, "Get global feed")
            postDao.getGlobalFeed(
                isPosts = isPosts,
                isReplies = isReplies,
                until = until,
                limit = limit,
            )
        } else if (authorPubkeys == null && relays != null) {
            Log.d(TAG, "Get global feed by relays $relays")
            if (relays.isEmpty()) listOf()
            else postDao.getGlobalFeedByRelays(
                isPosts = isPosts,
                isReplies = isReplies,
                relays = relays,
                until = until,
                limit = limit,
            )
        } else if (authorPubkeys != null && relays == null) {
            Log.d(TAG, "Get authored feed")
            if (authorPubkeys.isEmpty()) listOf()
            else postDao.getAuthoredFeed(
                isPosts = isPosts,
                isReplies = isReplies,
                authorPubkeys = authorPubkeys,
                until = until,
                limit = limit,
            )
        } else if (authorPubkeys != null && relays != null) {
            Log.d(TAG, "Get authored feed by relays")
            if (authorPubkeys.isEmpty() || relays.isEmpty()) listOf()
            else postDao.getAuthoredFeedByRelays(
                isPosts = isPosts,
                isReplies = isReplies,
                authorPubkeys = authorPubkeys,
                relays = relays,
                until = until,
                limit = limit,
            )
        } else {
            Log.w(TAG, "Could not find correct db call. Default to empty list")
            listOf()
        }
    }
}
