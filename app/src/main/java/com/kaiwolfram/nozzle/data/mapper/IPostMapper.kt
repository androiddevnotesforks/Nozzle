package com.kaiwolfram.nozzle.data.mapper

import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.model.PostWithMeta
import kotlinx.coroutines.flow.Flow

interface IPostMapper {
   suspend fun mapToPostsWithMetaFlow(posts: List<PostEntity>): Flow<List<PostWithMeta>>
}
