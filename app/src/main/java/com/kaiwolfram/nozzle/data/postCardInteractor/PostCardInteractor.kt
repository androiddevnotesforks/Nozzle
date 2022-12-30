package com.kaiwolfram.nozzle.data.postCardInteractor

import android.util.Log
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.preferences.key.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ReactionDao
import com.kaiwolfram.nozzle.data.room.dao.RepostDao

private const val TAG = "PostCardInteractor"

class PostCardInteractor(
    private val nostrService: INostrService,
    private val pubkeyProvider: IPubkeyProvider,
    private val reactionDao: ReactionDao,
    private val repostDao: RepostDao,
) : IPostCardInteractor {

    init {
        Log.i(TAG, "Initialize PostCardInteractor")
    }

    override suspend fun like(postId: String) {
        Log.i(TAG, "Like $postId")
        nostrService.sendLike(postId)
        reactionDao.like(pubkey = pubkeyProvider.getPubkey(), eventId = postId)
    }

    override suspend fun repost(postId: String) {
        Log.i(TAG, "Repost $postId")
        nostrService.sendRepost(postId = postId, quote = "")
        repostDao.repost(eventId = postId, pubkey = pubkeyProvider.getPubkey())
    }

    override suspend fun reply(replyTo: String, content: String) {
        TODO("Not yet implemented")
    }
}
