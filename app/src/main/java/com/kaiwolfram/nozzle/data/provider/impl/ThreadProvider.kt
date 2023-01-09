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
    override suspend fun getThread(currentEventId: String): PostThread {
        val wholeThread = postDao.getWholeThread(currentEventId)
        val current = wholeThread.find { it.id == currentEventId }
            ?: return PostThread.createEmpty()
        val previous = getPrevious(wholeThread, current)
        val replies = wholeThread.filter { it.replyToId == current.id }

        return getMappedThread(current, previous, replies)
    }

    private fun getPrevious(wholeThread: List<PostEntity>, current: PostEntity): List<PostEntity> {
        val previous = mutableListOf(current)
        var isEarliest = false
        while (!isEarliest) {
            for (post in wholeThread) {
                if (post.id == current.replyToRootId) isEarliest = true
                if (post.id == previous.last().replyToId) {
                    previous.add(post)
                    if (post.replyToId == null) isEarliest = true
                    break
                }
                if (post == wholeThread.last()) {
                    isEarliest = true
                }
            }
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
