package com.kaiwolfram.nozzle.data.provider.impl

import com.kaiwolfram.nozzle.data.provider.IPostProvider
import com.kaiwolfram.nozzle.model.RepostPreview

class PostProvider : IPostProvider {
    // ::repost is null
    override fun getRepostsMap(repostedIds: List<String>): Map<String, RepostPreview> {
        TODO("Not yet implemented")
    }
}
