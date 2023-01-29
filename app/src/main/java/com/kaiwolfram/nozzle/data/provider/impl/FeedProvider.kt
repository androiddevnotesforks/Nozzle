package com.kaiwolfram.nozzle.data.provider.impl

import android.util.Log
import com.kaiwolfram.nozzle.data.mapper.IPostMapper
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.model.PostWithMeta
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val TAG = "FeedProvider"
private const val EMIT_INTERVAL_TIME = 1500L
private const val EMIT_INTERVAL_NUM = 10L

class FeedProvider(
    private val pubkeyProvider: IPubkeyProvider,
    private val postMapper: IPostMapper,
    private val nostrSubscriber: INostrSubscriber,
    private val postDao: PostDao,
) : IFeedProvider {

    override suspend fun getFeed(limit: Int, until: Long?): List<PostWithMeta> {
        Log.i(TAG, "Get feed")
        val posts = postDao.getLatestFeed(
            pubkey = pubkeyProvider.getPubkey(),
            limit = limit,
            until = until
        )

        return postMapper.mapToPostsWithMeta(posts)
    }

    override suspend fun getFeedByRelay(
        relayUrl: String,
        limit: Int,
        until: Long?
    ): List<PostWithMeta> {
        Log.i(TAG, "Get feed by relay $relayUrl")
        val posts = postDao.getLatestFeedByRelay(
            pubkey = pubkeyProvider.getPubkey(),
            relayUrl = relayUrl,
            limit = limit,
            until = until
        )

        return postMapper.mapToPostsWithMeta(posts)
    }

    override fun getFeedWithSingleAuthor(
        pubkey: String,
        limit: Int,
        until: Long?,
    ): Flow<List<PostWithMeta>> {
        Log.i(TAG, "Get feed of author $pubkey")
        return flow {
            nostrSubscriber.unsubscribeFeeds()
            nostrSubscriber.unsubscribeAdditionalPostsData()
            nostrSubscriber.subscribeToFeed(
                authorPubkeys = listOf(pubkey),
                limit = 2 * limit,
                until = until
            )
            val posts = postDao.getLatestFeedOfSingleAuthor(
                pubkey = pubkey,
                limit = limit,
                until = until
            )

            if (posts.isNotEmpty()) {
                postMapper.mapToPostsWithMeta(posts).let { mapped ->
                    emit(mapped)
                    nostrSubscriber.subscribeToAdditionalPostsData(posts = mapped)
                }
                for (num in 1..EMIT_INTERVAL_NUM) {
                    delay(EMIT_INTERVAL_TIME)
                    nostrSubscriber.unsubscribeAdditionalPostsData()
                    postMapper.mapToPostsWithMeta(posts).let { mapped ->
                        emit(mapped)
                        nostrSubscriber.subscribeToAdditionalPostsData(posts = mapped)
                    }
                }
            }
        }
    }

    override suspend fun appendFeedByRelay(
        relayUrl: String,
        currentFeed: List<PostWithMeta>,
        limit: Int
    ): List<PostWithMeta> {
        Log.i(TAG, "Append feed from $relayUrl")
        currentFeed.lastOrNull()?.let { last ->
            val allPosts = mutableListOf<PostWithMeta>()
            allPosts.addAll(currentFeed)
            allPosts.addAll(
                getFeedByRelay(
                    relayUrl = relayUrl,
                    limit = limit,
                    until = last.createdAt
                )
            )
            return allPosts
        }

        return listOf()
    }

    override fun appendFeedWithSingleAuthor(
        pubkey: String,
        currentFeed: List<PostWithMeta>,
        limit: Int,
    ): Flow<List<PostWithMeta>> {
        Log.i(TAG, "Append to feed of author $pubkey")
        if (currentFeed.isEmpty()) return flow { emit(listOf()) }

        return flow {
            currentFeed.lastOrNull()?.let { last ->
                nostrSubscriber.unsubscribeFeeds()
                nostrSubscriber.unsubscribeAdditionalPostsData()
                nostrSubscriber.subscribeToFeed(
                    authorPubkeys = listOf(pubkey),
                    limit = 2 * limit,
                    until = last.createdAt
                )
                delay(1000)

                val toAppend = postDao.getLatestFeedOfSingleAuthor(
                    pubkey = pubkey,
                    limit = limit,
                    until = last.createdAt
                )
                if (toAppend.isNotEmpty()) {
                    postMapper.mapToPostsWithMeta(toAppend).let { mapped ->
                        Log.d(TAG, "Emit ${currentFeed.size} + ${mapped.size}")
                        emit(currentFeed + mapped)
                        nostrSubscriber.subscribeToAdditionalPostsData(posts = mapped)
                    }
                    for (num in 1..EMIT_INTERVAL_NUM) {
                        delay(EMIT_INTERVAL_TIME)
                        nostrSubscriber.unsubscribeAdditionalPostsData()
                        postMapper.mapToPostsWithMeta(toAppend).let { mapped ->
                            Log.d(TAG, "Emit ${currentFeed.size} + ${mapped.size}")
                            emit(currentFeed + mapped)
                            nostrSubscriber.subscribeToAdditionalPostsData(posts = mapped)
                        }
                    }
                }
            }
        }
    }
}
