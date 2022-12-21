package com.kaiwolfram.nozzle.data.postCardInteractor

import android.util.Log
import com.kaiwolfram.nozzle.data.nostr.INostrRepository
import com.kaiwolfram.nozzle.data.room.dao.ReactionDao

private const val TAG = "PostCardInteractor"

class PostCardInteractor(
    private val nostrRepository: INostrRepository,
    private val reactionDao: ReactionDao
) : IPostCardInteractor {

    init {
        Log.i(TAG, "Initialize PostCardInteractor")
    }

    override suspend fun like(pubkey: String, postId: String) {
        Log.i(TAG, "$pubkey likes $postId")
        nostrRepository.likePost(postId)
        reactionDao.like(pubkey = pubkey, eventId = postId)
    }

    override suspend fun repost(pubkey: String, postId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun reply(pubkey: String, replyTo: String, content: String) {
        TODO("Not yet implemented")
    }
}
