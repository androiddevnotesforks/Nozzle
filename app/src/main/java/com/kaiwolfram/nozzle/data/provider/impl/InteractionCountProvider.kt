package com.kaiwolfram.nozzle.data.provider.impl

import com.kaiwolfram.nozzle.data.provider.IInteractionCountProvider
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.dao.ReactionDao
import com.kaiwolfram.nozzle.data.room.dao.RepostDao
import com.kaiwolfram.nozzle.model.InteractionCounts

class InteractionCountProvider(
    reactionDao: ReactionDao,
    repostDao: RepostDao,
    postDao: PostDao,
) : IInteractionCountProvider {
    override fun getCounts(postIds: List<String>): InteractionCounts {
        TODO()
    }
}
