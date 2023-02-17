package com.kaiwolfram.nozzle.data.provider.impl

import android.util.Log
import com.kaiwolfram.nostrclientkt.model.AllRelays
import com.kaiwolfram.nostrclientkt.model.MultipleRelays
import com.kaiwolfram.nostrclientkt.model.RelaySelection
import com.kaiwolfram.nozzle.data.mapper.IPostMapper
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.data.utils.getCurrentTimeInSeconds
import com.kaiwolfram.nozzle.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val TAG = "FeedProvider"
private const val EMIT_INTERVAL_TIME = 2000L
private const val RESUB_AFTER_INTERVAL = 3

class FeedProvider(
    private val pubkeyProvider: IPubkeyProvider,
    private val postMapper: IPostMapper,
    private val nostrSubscriber: INostrSubscriber,
    private val postDao: PostDao,
    private val contactDao: ContactDao,
) : IFeedProvider {

    override fun getFeed(
        feedSettings: FeedSettings,
        limit: Int,
        until: Long?,
    ): Flow<List<PostWithMeta>> {
        Log.i(TAG, "Get feed")
        return flow {
            nostrSubscriber.unsubscribeFeeds()
            nostrSubscriber.unsubscribeAdditionalPostsData()
            val authorPubkeys = listPubkeys(authorSelection = feedSettings.authorSelection)
            val relays = listRelays(relaySelection = feedSettings.relaySelection)
            nostrSubscriber.subscribeToFeed(
                authorPubkeys = authorPubkeys,
                limit = 2 * limit,
                until = until,
                relaySelection = feedSettings.relaySelection
            )
            delay(1000)
            val posts = listPosts(
                isPosts = feedSettings.isPosts,
                isReplies = feedSettings.isReplies,
                authorPubkeys = authorPubkeys,
                relays = relays,
                until = until ?: getCurrentTimeInSeconds(),
                limit = limit,
            )

            if (posts.isNotEmpty()) {
                postMapper.mapToPostsWithMeta(posts).let { mapped ->
                    emit(mapped)
                    nostrSubscriber.subscribeToAdditionalPostsData(posts = mapped)
                }
                // TODO: Use Flow
                var counter = 0
                while (true) {
                    delay(EMIT_INTERVAL_TIME)
                    postMapper.mapToPostsWithMeta(posts).let { mapped ->
                        Log.d(TAG, "Emit ${mapped.size}")
                        emit(mapped)
                        if (counter == RESUB_AFTER_INTERVAL) {
                            Log.d(TAG, "Resub after $counter iterations")
                            nostrSubscriber.unsubscribeAdditionalPostsData()
                            nostrSubscriber.subscribeToAdditionalPostsData(posts = mapped)
                        }
                        counter++
                    }
                }
            } else {
                emit(listOf())
            }
        }
    }

    private suspend fun listPubkeys(authorSelection: AuthorSelection): List<String>? {
        return when (authorSelection) {
            is Everyone -> null
            is Contacts -> contactDao.listContactPubkeys(pubkeyProvider.getPubkey())
            is SingleAuthor -> listOf(authorSelection.pubkey)
        }
    }

    private fun listRelays(relaySelection: RelaySelection): List<String>? {
        return when (relaySelection) {
            is AllRelays -> null
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
