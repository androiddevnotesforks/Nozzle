package com.kaiwolfram.nozzle.data.provider

import android.util.Log
import com.kaiwolfram.nozzle.data.defaultPubkeys
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.model.PostWithMeta

private const val TAG = "FeedProvider"

// TODO: Get more data from db
class FeedProvider(
    private val pubkeyProvider: IPubkeyProvider,
    private val postDao: PostDao,
    private val contactDao: ContactDao
) : IFeedProvider {

    override suspend fun getFeed(): List<PostWithMeta> {
        Log.i(TAG, "Get feed")
        val followingCount = contactDao.getNumberOfFollowing(pubkey = pubkeyProvider.getPubkey())
        val posts = if (followingCount > 0) {
            postDao.getLatestFeed(pubkey = pubkeyProvider.getPubkey())
        } else {
            Log.i(TAG, "Use default contacts")
            postDao.getLatestFeedOfCustomContacts(contactPubkeys = defaultPubkeys)
        }.reversed()

        return posts.map {
            PostWithMeta(
                id = it.id,
                replyToId = it.replyTo,
                replyToRootId = it.replyToRoot,
                pubkey = it.pubkey,
                createdAt = it.createdAt,
                content = it.content,
                name = "TODO",
                pictureUrl = "https://64.media.tumblr.com/a727acf2c19888056b03500a89227cd4/0f1f0b7b20b511df-c9/s400x600/afeb2ab1cf61c2e4e93b6fba00c983a6a8cb9d60.gifv",
                replyToName = "TODO",
                relayUrl = "TODO",
                isLikedByMe = false,
                isRepostedByMe = false,
                referencePost = null,
                numOfLikes = 0, // TODO: Real values
                numOfReposts = 0,
                numOfReplies = 0,
            )
        }
    }

    override suspend fun getFeedSince(since: Long): List<PostWithMeta> {
        val followingCount = contactDao.getNumberOfFollowing(pubkey = pubkeyProvider.getPubkey())
        val posts = if (followingCount == 0) {
            postDao.getFeedSince(pubkey = pubkeyProvider.getPubkey(), since = since)
        } else {
            Log.i(TAG, "Use default contacts")
            postDao.getFeedOfCustomContactsSince(contactPubkeys = defaultPubkeys, since = since)
        }

        Log.i(TAG, "Fetched feed with ${posts.size} posts since $since")

        return posts.map {
            PostWithMeta(
                id = it.id,
                replyToId = it.replyTo,
                replyToRootId = it.replyToRoot,
                pubkey = it.pubkey,
                createdAt = it.createdAt,
                content = it.content,
                replyToName = "TODO",
                relayUrl = "TODO",
                name = "TODO",
                pictureUrl = "https://64.media.tumblr.com/a727acf2c19888056b03500a89227cd4/0f1f0b7b20b511df-c9/s400x600/afeb2ab1cf61c2e4e93b6fba00c983a6a8cb9d60.gifv",
                isLikedByMe = false,
                isRepostedByMe = false,
                referencePost = null,
                numOfLikes = 0, // TODO: Real values
                numOfReposts = 0,
                numOfReplies = 0,
            )
        }
    }

    override suspend fun getLatestTimestamp(): Long? {
        return postDao.getLatestTimestampOfFeed(pubkey = pubkeyProvider.getPubkey())
    }

    override suspend fun getFeedWithSingleAuthor(pubkey: String): List<PostWithMeta> {
        TODO("Not yet implemented")
    }

}
