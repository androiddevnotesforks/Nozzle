package com.kaiwolfram.nozzle.data.postCardInteractor

import android.util.Log
import com.kaiwolfram.nozzle.data.nostr.INostrRepository
import com.kaiwolfram.nozzle.data.room.dao.ReactionDao
import com.kaiwolfram.nozzle.data.room.dao.RepostDao

private const val TAG = "PostCardInteractor"

class PostCardInteractor(
    private val nostrRepository: INostrRepository,
    private val reactionDao: ReactionDao,
    private val repostDao: RepostDao,
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
        Log.i(TAG, "$pubkey reposts $postId")
        nostrRepository.repost(postId)
        repostDao.repost(eventId = postId, pubkey = pubkey)
    }

    override suspend fun reply(pubkey: String, replyTo: String, content: String) {
        TODO("Not yet implemented")
    }
}
