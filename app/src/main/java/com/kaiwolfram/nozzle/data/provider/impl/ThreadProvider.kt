package com.kaiwolfram.nozzle.data.provider.impl

import com.kaiwolfram.nozzle.data.mapper.IPostMapper
import com.kaiwolfram.nozzle.data.provider.IThreadProvider
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.model.PostThread

class ThreadProvider(
    private val postMapper: IPostMapper,
    private val postDao: PostDao,
) : IThreadProvider {
    override suspend fun getThread(currentPostId: String, replyToId: String?): PostThread {
        val threadEnd = postDao.getThreadEnd(
            currentPostId = currentPostId,
            replyToId = replyToId,
        )
        val current = threadEnd.find { it.id == currentPostId } ?: return PostThread.createEmpty()
        val replies = threadEnd.filter { it.replyToId == current.id }
        val previous = listPrevious(current)

        return getMappedThread(current, previous, replies)
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

    private suspend fun getMappedThread(
        current: PostEntity,
        previous: List<PostEntity>,
        replies: List<PostEntity>
    ): PostThread {
        val relevantPosts = listOf(listOf(current), previous, replies).flatten()
        val mapped = postMapper.mapToPostsWithMeta(relevantPosts)

        return PostThread(
            current = mapped.first(),
            previous = if (previous.isNotEmpty())
                mapped.subList(1, previous.size + 1) else listOf(),
            replies = mapped.takeLast(replies.size)
        )
    }
}
