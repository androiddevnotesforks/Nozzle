package com.kaiwolfram.nozzle.data.provider.impl

import com.kaiwolfram.nozzle.data.provider.IPostProvider
import com.kaiwolfram.nozzle.data.room.dao.RepostDao
import com.kaiwolfram.nozzle.model.RepostPreview

class PostProvider(private val repostDao: RepostDao) : IPostProvider {

    override suspend fun getRepostsMap(repostedIds: List<String>): Map<String, RepostPreview> {
        TODO("Not yet implemented")
        // Subscribe when id not found
    }
}
