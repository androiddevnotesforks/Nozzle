package com.kaiwolfram.nozzle.data.postCardInteractor

interface IPostCardInteractor {
    suspend fun like(postId: String, postPubkey: String)
    suspend fun repost(postId: String)
}
