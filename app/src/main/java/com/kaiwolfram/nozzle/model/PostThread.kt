package com.kaiwolfram.nozzle.model

data class PostThread(
    val current: PostWithMeta?,
    val previous: List<PostWithMeta>,
    val replies: List<PostWithMeta>
) {
    companion object {
        fun createEmpty(): PostThread {
            return PostThread(current = null, previous = listOf(), replies = listOf())
        }
    }
}
