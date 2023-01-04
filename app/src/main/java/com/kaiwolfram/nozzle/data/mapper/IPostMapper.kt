package com.kaiwolfram.nozzle.data.mapper

import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.model.PostWithMeta

interface IPostMapper {
   suspend fun mapToPostsWithMeta(posts: List<PostEntity>): List<PostWithMeta>
}
