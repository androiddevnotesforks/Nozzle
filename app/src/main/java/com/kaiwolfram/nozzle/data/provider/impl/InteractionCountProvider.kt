package com.kaiwolfram.nozzle.data.provider.impl

import com.kaiwolfram.nozzle.data.provider.IInteractionStatsProvider
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.dao.ReactionDao
import com.kaiwolfram.nozzle.model.InteractionStats

class InteractionStatsProvider(
    private val pubkeyProvider: IPubkeyProvider,
    private val reactionDao: ReactionDao,
    private val postDao: PostDao,
) : IInteractionStatsProvider {
    override suspend fun getStats(postIds: List<String>): InteractionStats {
        val numOfLikesPerPost = reactionDao.getNumOfLikesPerPost(postIds)
        val numOfRepostsPerPost = postDao.getNumOfRepostsPerPost(postIds)
        val numOfRepliesPerPost = postDao.getNumOfRepliesPerPost(postIds)
        val likedByMe = reactionDao.listLikedBy(pubkeyProvider.getPubkey(), postIds)
        val repostedByMe = postDao.listRepostedByPubkey(pubkeyProvider.getPubkey(), postIds)

        return InteractionStats(
            numOfLikesPerPost = numOfLikesPerPost,
            numOfRepostsPerPost = numOfRepostsPerPost,
            numOfRepliesPerPost = numOfRepliesPerPost,
            likedByMe = likedByMe,
            repostedByMe = repostedByMe,
        )
    }
}
