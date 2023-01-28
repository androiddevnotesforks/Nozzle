package com.kaiwolfram.nozzle.data.mapper

import com.kaiwolfram.nozzle.data.provider.IInteractionStatsProvider
import com.kaiwolfram.nozzle.data.room.dao.EventRelayDao
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.model.PostWithMeta

class PostMapper(
    private val interactionStatsProvider: IInteractionStatsProvider,
    private val postDao: PostDao,
    private val profileDao: ProfileDao,
    private val eventRelayDao: EventRelayDao,
) : IPostMapper {

    override suspend fun mapToPostsWithMeta(posts: List<PostEntity>): List<PostWithMeta> {
        if (posts.isEmpty()) return listOf()

        val postIds = posts.map { it.id }
        val stats = interactionStatsProvider.getStats(postIds)
        val reposts = postDao.getRepostsPreviewMap(posts.mapNotNull { it.repostedId })
        val namesAndPictures = profileDao.getNamesAndPicturesMap(posts.map { it.pubkey })
        val replyRecipients = profileDao.getAuthorNamesAndPubkeysMap(
            posts.mapNotNull { it.replyToId }
        )
        val relays = eventRelayDao.getRelayMap(postIds)

        return posts.map {
            PostWithMeta(
                id = it.id,
                replyToId = it.replyToId,
                replyToRootId = it.replyToRootId,
                replyToName = replyRecipients[it.replyToId]?.name,
                replyToPubkey = replyRecipients[it.replyToId]?.pubkey,
                pubkey = it.pubkey,
                createdAt = it.createdAt,
                content = it.content,
                name = namesAndPictures[it.pubkey]?.name.orEmpty(),
                pictureUrl = namesAndPictures[it.pubkey]?.picture.orEmpty(),
                repost = it.repostedId?.let { repostedId -> reposts[repostedId] },
                isLikedByMe = stats.isLikedByMe(it.id),
                isRepostedByMe = stats.isRepostedByMe(it.id),
                numOfLikes = stats.getNumOfLikes(it.id),
                numOfReposts = stats.getNumOfReposts(it.id),
                numOfReplies = stats.getNumOfReplies(it.id),
                relays = relays[it.id].orEmpty(),
            )
        }
    }
}
