package com.kaiwolfram.nozzle.data.postCardInteractor

import android.util.Log
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.dao.ReactionDao
import com.kaiwolfram.nozzle.data.room.entity.PostEntity

private const val TAG = "PostCardInteractor"

class PostCardInteractor(
    private val nostrService: INostrService,
    private val reactionDao: ReactionDao,
    private val postDao: PostDao,
) : IPostCardInteractor {

    override suspend fun like(postId: String, postPubkey: String) {
        Log.i(TAG, "Like $postId")
        val event = nostrService.sendLike(postId, postPubkey)
        reactionDao.like(pubkey = event.pubkey, eventId = postId)
    }

    override suspend fun repost(postId: String) {
        Log.i(TAG, "Repost $postId")
        val event = nostrService.sendRepost(postId = postId, quote = "")
        postDao.insertIfNotPresent(PostEntity.fromEvent(event))
    }
}
