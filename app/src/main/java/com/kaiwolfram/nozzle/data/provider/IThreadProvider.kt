package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.PostIds
import com.kaiwolfram.nozzle.model.PostThread

interface IThreadProvider {
    suspend fun getThread(ids: PostIds): PostThread
}
