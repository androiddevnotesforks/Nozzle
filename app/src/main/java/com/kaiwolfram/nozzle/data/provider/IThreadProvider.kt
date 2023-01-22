package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.PostThread

interface IThreadProvider {
    suspend fun getThread(currentPostId: String, replyToId: String?): PostThread
}
