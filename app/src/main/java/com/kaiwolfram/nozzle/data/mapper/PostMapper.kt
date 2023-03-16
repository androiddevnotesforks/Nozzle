package com.kaiwolfram.nozzle.data.mapper

import com.kaiwolfram.nozzle.data.provider.IInteractionStatsProvider
import com.kaiwolfram.nozzle.data.room.dao.EventRelayDao
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.model.RepostPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class PostMapper(
    private val interactionStatsProvider: IInteractionStatsProvider,
    private val postDao: PostDao,
    private val profileDao: ProfileDao,
    private val eventRelayDao: EventRelayDao,
) : IPostMapper {

    override suspend fun mapToPostsWithMetaFlow(posts: List<PostEntity>): Flow<List<PostWithMeta>> {
        if (posts.isEmpty()) return flow { emit(listOf()) }

        val postIds = posts.map { it.id }
        val statsFlow = interactionStatsProvider.getStatsFlow(postIds).distinctUntilChanged()
        val repostsFlow = postDao.getRepostsPreviewMapFlow(posts.mapNotNull { it.repostedId })
            .distinctUntilChanged()
        val namesAndPicturesFlow =
            profileDao.getNamesAndPicturesMapFlow(posts.map { it.pubkey }).distinctUntilChanged()
        val replyRecipientsFlow =
            profileDao.getAuthorNamesAndPubkeysMapFlow(posts.mapNotNull { it.replyToId })
                .distinctUntilChanged()
        val relaysFlow = eventRelayDao.getRelaysPerEventIdMapFlow(postIds).distinctUntilChanged()

        val mainFlow = flow {
            emit(posts.map {
                PostWithMeta(
                    id = it.id,
                    replyToId = it.replyToId,
                    replyToRootId = it.replyToRootId,
                    replyToName = "",
                    replyToPubkey = "",
                    replyRelayHint = it.replyRelayHint,
                    pubkey = it.pubkey,
                    createdAt = it.createdAt,
                    content = it.content,
                    name = "",
                    pictureUrl = "",
                    repost = it.repostedId?.let { repostedId ->
                        RepostPreview(
                            id = repostedId, pubkey = "",
                            content = "",
                            name = "",
                            picture = "",
                            createdAt = 0,
                        )
                    },
                    isLikedByMe = false,
                    isRepostedByMe = false,
                    numOfLikes = 0,
                    numOfReposts = 0,
                    numOfReplies = 0,
                    relays = listOf(),
                )
            })
        }

        return mainFlow.combine(statsFlow) { main, stats ->
            main.map {
                it.copy(
                    isLikedByMe = stats.isLikedByMe(it.id),
                    isRepostedByMe = stats.isRepostedByMe(it.id),
                    numOfLikes = stats.getNumOfLikes(it.id),
                    numOfReposts = stats.getNumOfReposts(it.id),
                    numOfReplies = stats.getNumOfReplies(it.id),
                )
            }
        }.combine(repostsFlow) { main, reposts ->
            main.map {
                it.copy(
                    repost = it.repost?.id.let { repostedId -> reposts[repostedId] },
                )
            }
        }.combine(namesAndPicturesFlow) { main, namesAndPictures ->
            main.map {
                it.copy(
                    pictureUrl = namesAndPictures[it.pubkey]?.picture.orEmpty(),
                    name = namesAndPictures[it.pubkey]?.name.orEmpty(),
                )
            }
        }.combine(replyRecipientsFlow) { main, replyRecipients ->
            main.map {
                it.copy(
                    replyToName = replyRecipients[it.replyToId]?.name,
                    replyToPubkey = replyRecipients[it.replyToId]?.pubkey,
                )
            }
        }.combine(relaysFlow) { main, relays ->
            main.map {
                it.copy(
                    relays = relays[it.id].orEmpty(),
                )
            }
        }
    }
}
