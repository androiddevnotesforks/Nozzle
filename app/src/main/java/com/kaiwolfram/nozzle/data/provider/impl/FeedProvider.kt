package com.kaiwolfram.nozzle.data.provider.impl

import android.util.Log
import com.kaiwolfram.nozzle.data.mapper.IPostMapper
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.model.PostWithMeta

private const val TAG = "FeedProvider"

class FeedProvider(
    private val pubkeyProvider: IPubkeyProvider,
    private val postMapper: IPostMapper,
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

    override suspend fun getFeedWithSingleAuthor(
        pubkey: String,
        limit: Int,
        until: Long?
    ): List<PostWithMeta> {
        Log.i(TAG, "Get feed of author $pubkey")
        val posts = postDao.getLatestFeedOfSingleAuthor(
            pubkey = pubkey,
            limit = limit,
            until = until
        )

        return postMapper.mapToPostsWithMeta(posts)
    }

    override suspend fun appendFeed(
        currentFeed: List<PostWithMeta>,
        limit: Int
    ): List<PostWithMeta> {
        currentFeed.lastOrNull()?.let { last ->
            val allPosts = mutableListOf<PostWithMeta>()
            allPosts.addAll(currentFeed)
            allPosts.addAll(getFeed(limit = limit, until = last.createdAt))
            return allPosts
        }

        return listOf()
    }

    override suspend fun appendFeedWithSingleAuthor(
        pubkey: String,
        currentFeed: List<PostWithMeta>,
        limit: Int
    ): List<PostWithMeta> {
        currentFeed.lastOrNull()?.let { last ->
            val allPosts = mutableListOf<PostWithMeta>()
            allPosts.addAll(currentFeed)
            allPosts.addAll(
                getFeedWithSingleAuthor(
                    pubkey = pubkey,
                    limit = limit,
                    until = last.createdAt
                )
            )
            return allPosts
        }

        return listOf()
    }
}
