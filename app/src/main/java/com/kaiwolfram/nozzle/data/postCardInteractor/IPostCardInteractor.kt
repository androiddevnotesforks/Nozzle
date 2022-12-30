package com.kaiwolfram.nozzle.data.postCardInteractor

interface IPostCardInteractor {
    suspend fun like(postId: String)
    suspend fun repost(postId: String)
    suspend fun reply(replyTo: String, content: String)
}
