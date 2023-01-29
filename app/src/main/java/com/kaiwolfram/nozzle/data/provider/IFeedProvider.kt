package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.PostWithMeta
import kotlinx.coroutines.flow.Flow

interface IFeedProvider {
    suspend fun getFeed(limit: Int, until: Long? = null): List<PostWithMeta>

    suspend fun getFeedByRelay(
        relayUrl: String,
        limit: Int,
        until: Long? = null
    ): List<PostWithMeta>

    fun getFeedWithSingleAuthor(
        pubkey: String,
        limit: Int,
        until: Long? = null,
    ): Flow<List<PostWithMeta>>

    suspend fun appendFeedByRelay(
        relayUrl: String,
        currentFeed: List<PostWithMeta>,
        limit: Int
    ): List<PostWithMeta>

    fun appendFeedWithSingleAuthor(
        pubkey: String,
        currentFeed: List<PostWithMeta>,
        limit: Int,
    ): Flow<List<PostWithMeta>>
}
