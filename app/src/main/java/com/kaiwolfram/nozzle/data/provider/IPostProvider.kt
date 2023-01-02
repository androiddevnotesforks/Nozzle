package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.RepostPreview

interface IPostProvider {
    fun getRepostsMap(repostedIds: List<String>): Map<String, RepostPreview>
}
