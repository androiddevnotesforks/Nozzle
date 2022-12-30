package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.PostWithMeta

interface IFeedProvider {
    // TODO: Sort from old to new
    suspend fun getFeed(pubkey: String): List<PostWithMeta>
    suspend fun getFeedSince(pubkey: String, since: Long): List<PostWithMeta>
    suspend fun getFeedWithSingleAuthor(pubkey: String): List<PostWithMeta>
}
