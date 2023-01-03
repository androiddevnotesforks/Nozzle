package com.kaiwolfram.nozzle.data.provider.impl

import com.kaiwolfram.nozzle.data.provider.IInteractionStatsProvider
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ReactionDao
import com.kaiwolfram.nozzle.data.room.dao.ReplyDao
import com.kaiwolfram.nozzle.data.room.dao.RepostDao
import com.kaiwolfram.nozzle.model.InteractionStats

class InteractionStatsProvider(
    private val pubkeyProvider: IPubkeyProvider,
    private val reactionDao: ReactionDao,
    private val repostDao: RepostDao,
    private val replyDao: ReplyDao,
) : IInteractionStatsProvider {
    override suspend fun getStats(postIds: List<String>): InteractionStats {
        val numOfLikesPerPost = reactionDao.getNumOfLikesPerPost(postIds)
        val numOfRepostsPerPost = repostDao.getNumOfRepostsPerPost(postIds)
        val numOfRepliesPerPost = replyDao.getNumOfRepliesPerPost(postIds)
        val likedByMe = reactionDao.listLikedBy(pubkeyProvider.getPubkey(), postIds)
        val repostedByMe = repostDao.listRepostedByMe(pubkeyProvider.getPubkey(), postIds)

        return InteractionStats(
            numOfLikesPerPost = numOfLikesPerPost,
            numOfRepostsPerPost = numOfRepostsPerPost,
            numOfRepliesPerPost = numOfRepliesPerPost,
            likedByMe = likedByMe,
            repostedByMe = repostedByMe,
        )
    }
}
