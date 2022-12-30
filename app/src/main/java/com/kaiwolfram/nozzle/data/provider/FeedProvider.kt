package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.PostWithMeta

private const val TAG = "FeedProvider"

class FeedProvider : IFeedProvider {
    // TODO: Sort by ???
    override suspend fun getFeed(pubkey: String): List<PostWithMeta> {
        TODO("Not yet implemented")
    }

    override suspend fun getFeedSince(pubkey: String, since: Long): List<PostWithMeta> {
        TODO("Not yet implemented")
    }

    override suspend fun getFeedWithSingleAuthor(pubkey: String): List<PostWithMeta> {
        TODO("Not yet implemented")
    }

}
