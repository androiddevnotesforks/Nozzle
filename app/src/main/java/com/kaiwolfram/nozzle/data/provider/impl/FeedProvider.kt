package com.kaiwolfram.nozzle.data.provider.impl

import android.util.Log
import com.kaiwolfram.nozzle.data.defaultPubkeys
import com.kaiwolfram.nozzle.data.mapper.IPostMapper
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.model.PostWithMeta

private const val TAG = "FeedProvider"

class FeedProvider(
    private val pubkeyProvider: IPubkeyProvider,
    private val postMapper: IPostMapper,
    private val postDao: PostDao,
    private val contactDao: ContactDao,
) : IFeedProvider {

    override suspend fun getFeed(): List<PostWithMeta> {
        Log.i(TAG, "Get feed")
        val followingCount = contactDao.getNumberOfFollowing(pubkey = pubkeyProvider.getPubkey())
        val posts = if (followingCount > 0) {
            postDao.getLatestFeed(pubkey = pubkeyProvider.getPubkey())
        } else {
            Log.i(TAG, "Use default contacts")
            postDao.getLatestFeedOfCustomContacts(*defaultPubkeys.toTypedArray())
        }

        return postMapper.mapToPostsWithMeta(posts)
    }

    override suspend fun getFeedSince(since: Long): List<PostWithMeta> {
        Log.i(TAG, "Fetch feed since $since")
        val followingCount = contactDao.getNumberOfFollowing(pubkey = pubkeyProvider.getPubkey())
        val posts = if (followingCount == 0) {
            postDao.getFeedSince(pubkey = pubkeyProvider.getPubkey(), since = since)
        } else {
            Log.i(TAG, "Use default contacts")
            postDao.getFeedOfCustomContactsSince(contactPubkeys = defaultPubkeys, since = since)
        }

        return postMapper.mapToPostsWithMeta(posts)
    }

    override suspend fun getLatestTimestamp(): Long? {
        return postDao.getLatestTimestampOfFeed(pubkey = pubkeyProvider.getPubkey())
    }

    override suspend fun getFeedWithSingleAuthor(pubkey: String): List<PostWithMeta> {
        Log.i(TAG, "Get feed of author $pubkey")
        // TODO: Subscribe to pubkey
        val posts = postDao.getLatestFeedOfCustomContacts(pubkey)

        return postMapper.mapToPostsWithMeta(posts)
    }
}
