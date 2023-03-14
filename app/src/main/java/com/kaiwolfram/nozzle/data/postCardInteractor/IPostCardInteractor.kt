package com.kaiwolfram.nozzle.data.postCardInteractor

interface IPostCardInteractor {
    suspend fun like(postId: String, postPubkey: String, relays: Collection<String>?)
    suspend fun repost(postId: String, relays: Collection<String>?)
}
