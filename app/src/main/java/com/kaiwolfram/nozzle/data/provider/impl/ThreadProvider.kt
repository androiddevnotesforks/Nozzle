package com.kaiwolfram.nozzle.data.provider.impl

import com.kaiwolfram.nozzle.data.mapper.IPostMapper
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import com.kaiwolfram.nozzle.data.provider.IThreadProvider
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.model.PostThread
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ThreadProvider(
    private val postMapper: IPostMapper,
    private val nostrSubscriber: INostrSubscriber,
    private val postDao: PostDao,
) : IThreadProvider {
    override suspend fun getThreadFlow(
        currentPostId: String,
        replyToId: String?,
        waitForSubscription: Long?
    ): Flow<PostThread> {
        renewThreadSubscription(currentPostId = currentPostId, replyToId = replyToId)
        waitForSubscription?.let { delay(it) }

        val threadEnd = postDao.getThreadEnd(
            currentPostId = currentPostId,
            replyToId = replyToId,
        )
        val current = threadEnd.find { it.id == currentPostId }
            ?: return flow { emit(PostThread.createEmpty()) }
        val replies = threadEnd.filter { it.replyToId == current.id }
        val previous = listPrevious(current)

        return getMappedThreadFlow(current, previous, replies)
    }

    private fun renewThreadSubscription(currentPostId: String, replyToId: String?) {
        nostrSubscriber.unsubscribeThread()
        nostrSubscriber.subscribeToThread(
            currentPostId = currentPostId,
            replyToId = replyToId,
            replyToRootId = replyToId
        )
    }

    private suspend fun listPrevious(current: PostEntity): List<PostEntity> {
        if (current.replyToId == null) return listOf()

        val previous = mutableListOf(current)
        while (previous.last().replyToId != null) {
            val replyToId = previous.last().replyToId ?: break
            val previousPost = postDao.getPost(replyToId) ?: break
            previous.add(previousPost)
        }

        previous.reverse() // root first
        previous.removeLast() // Removing 'current'

        return previous
    }

    private suspend fun getMappedThreadFlow(
        current: PostEntity,
        previous: List<PostEntity>,
        replies: List<PostEntity>
    ): Flow<PostThread> {
        val relevantPosts = listOf(listOf(current), previous, replies).flatten()
        return postMapper.mapToPostsWithMetaFlow(relevantPosts).map {
            PostThread(
                current = it.first(),
                previous = if (previous.isNotEmpty())
                    it.subList(1, previous.size + 1) else listOf(),
                replies = it.takeLast(replies.size)
            )
        }
    }
}
