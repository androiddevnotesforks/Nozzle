package com.kaiwolfram.nozzle.data.mapper

import com.kaiwolfram.nozzle.data.provider.IInteractionStatsProvider
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.room.dao.RepostDao
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.model.PostWithMeta

class PostMapper(
    private val interactionStatsProvider: IInteractionStatsProvider,
    private val repostDao: RepostDao,
    private val profileDao: ProfileDao,
) : IPostMapper {

    override suspend fun mapToPostsWithMeta(posts: List<PostEntity>): List<PostWithMeta> {
        if (posts.isEmpty()) return listOf()

        val stats = interactionStatsProvider.getStats(posts.map { it.id })
        val reposts = repostDao.getRepostsMap(posts.mapNotNull { it.repostedId })
        val namesAndPictures = profileDao.getNamesAndPicturesMap(posts.map { it.pubkey })

        return posts.map {
            PostWithMeta(
                id = it.id,
                replyToId = it.replyToId,
                replyToRootId = it.replyToRootId,
                pubkey = it.pubkey,
                createdAt = it.createdAt,
                content = it.content,
                name = namesAndPictures[it.pubkey]?.name.orEmpty(),
                pictureUrl = namesAndPictures[it.pubkey]?.picture.orEmpty(),
                replyToName = "replyToId -> pubkey -> name", // TODO
                repost = it.repostedId?.let { repostedId -> reposts[repostedId] },
                isLikedByMe = stats.isLikedByMe(it.id),
                isRepostedByMe = stats.isRepostedByMe(it.id),
                numOfLikes = stats.getNumOfLikes(it.id),
                numOfReposts = stats.getNumOfReposts(it.id),
                numOfReplies = stats.getNumOfReplies(it.id),
            )
        }
    }
}
