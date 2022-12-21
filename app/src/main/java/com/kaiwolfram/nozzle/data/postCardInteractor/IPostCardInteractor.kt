package com.kaiwolfram.nozzle.data.postCardInteractor

interface IPostCardInteractor {
    suspend fun like(pubkey: String, postId: String)
    suspend fun repost(pubkey: String, postId: String)
    suspend fun reply(pubkey: String, replyTo: String, content: String)
}
