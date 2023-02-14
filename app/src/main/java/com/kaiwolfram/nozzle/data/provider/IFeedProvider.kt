package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.FeedSettings
import com.kaiwolfram.nozzle.model.PostWithMeta
import kotlinx.coroutines.flow.Flow

interface IFeedProvider {
    suspend fun getFeed(
        feedSettings: FeedSettings,
        limit: Int,
        until: Long? = null
    ): List<PostWithMeta>

    // TODO: This should utilize getFeed
    fun getFeedWithSingleAuthor(
        pubkey: String,
        limit: Int,
        until: Long? = null,
    ): Flow<List<PostWithMeta>>

    // TODO: wtf is this? Delete pls
    fun appendFeedWithSingleAuthor(
        pubkey: String,
        currentFeed: List<PostWithMeta>,
        limit: Int,
    ): Flow<List<PostWithMeta>>
}
