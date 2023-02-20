package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.FeedSettings
import com.kaiwolfram.nozzle.model.PostWithMeta
import kotlinx.coroutines.flow.Flow

interface IFeedProvider {
    suspend fun getFeedFlow(
        feedSettings: FeedSettings,
        limit: Int,
        until: Long? = null,
        waitForSubscription: Long? = null
    ): Flow<List<PostWithMeta>>
}
