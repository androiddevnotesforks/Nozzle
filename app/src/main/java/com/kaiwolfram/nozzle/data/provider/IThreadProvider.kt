package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.PostThread
import kotlinx.coroutines.flow.Flow

interface IThreadProvider {
    suspend fun getThreadFlow(
        currentPostId: String,
        replyToId: String?,
        waitForSubscription: Long? = null
    ): Flow<PostThread>
}
