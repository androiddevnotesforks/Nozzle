package com.kaiwolfram.nozzle.data.mapper

import com.kaiwolfram.nozzle.data.provider.IInteractionStatsProvider
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.model.PostWithMeta

class PostMapper(
    private val interactionStatsProvider: IInteractionStatsProvider,
    private val postDao: PostDao,
    private val profileDao: ProfileDao,
) : IPostMapper {

    override suspend fun mapToPostsWithMeta(posts: List<PostEntity>): List<PostWithMeta> {
        if (posts.isEmpty()) return listOf()

        val stats = interactionStatsProvider.getStats(posts.map { it.id })
        val reposts = postDao.getRepostsPreviewMap(posts.mapNotNull { it.repostedId })
        val namesAndPictures = profileDao.getNamesAndPicturesMap(posts.map { it.pubkey })
        val replyRecipients = profileDao.getAuthorNamesMap(posts.mapNotNull { it.replyToId })

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
                replyToName = replyRecipients[it.replyToId],
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
