package com.kaiwolfram.nozzle.model

data class PostThread(
    val current: PostWithMeta?,
    val previous: List<PostWithMeta>,
    val replies: List<PostWithMeta>
) {
    fun getList(): List<PostWithMeta> {
        val result = mutableListOf<PostWithMeta>()
        current?.let { result.add(it) }
        result.addAll(previous)
        result.addAll(replies)

        return result
    }

    companion object {
        fun createEmpty(): PostThread {
            return PostThread(current = null, previous = listOf(), replies = listOf())
        }
    }
}
