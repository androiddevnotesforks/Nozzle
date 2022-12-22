package com.kaiwolfram.nozzle.model

data class PostWithMeta(
    val id: String,
    val replyToId: String?,
    val replyToName: String?,
    val name: String,
    val pictureUrl: String,
    val pubkey: String,
    val createdAt: Long,
    val content: String,
    val isLikedByMe: Boolean,
    val isRepostedByMe: Boolean,
)
