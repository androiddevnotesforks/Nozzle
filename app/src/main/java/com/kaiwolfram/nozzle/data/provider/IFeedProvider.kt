package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.PostWithMeta

interface IFeedProvider {
    suspend fun getFeed(limit: Int, until: Long? = null): List<PostWithMeta>
    suspend fun getFeedWithSingleAuthor(
        pubkey: String,
        limit: Int,
        until: Long? = null
    ): List<PostWithMeta>

    suspend fun appendFeed(currentFeed: List<PostWithMeta>, limit: Int): List<PostWithMeta>
    suspend fun appendFeedWithSingleAuthor(
        pubkey: String,
        currentFeed: List<PostWithMeta>,
        limit: Int
    ): List<PostWithMeta>
}
