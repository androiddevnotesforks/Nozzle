package com.kaiwolfram.nozzle.data.provider.impl

import com.kaiwolfram.nozzle.data.provider.IInteractionStatsProvider
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.dao.ReactionDao
import com.kaiwolfram.nozzle.data.room.dao.RepostDao
import com.kaiwolfram.nozzle.model.InteractionStats

class InteractionStatsProvider(
    pubkeyProvider: IPubkeyProvider,
    reactionDao: ReactionDao,
    repostDao: RepostDao,
    postDao: PostDao,
) : IInteractionStatsProvider {
    override fun getStats(postIds: List<String>): InteractionStats {
        TODO()
    }
}
