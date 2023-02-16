package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.FeedSettings
import com.kaiwolfram.nozzle.model.PostWithMeta
import kotlinx.coroutines.flow.Flow

interface IFeedProvider {
    fun getFeed(
        feedSettings: FeedSettings,
        limit: Int,
        until: Long? = null,
    ): Flow<List<PostWithMeta>>
}
