package com.kaiwolfram.nozzle.model

data class PostWithMeta(
    val id: String,
    val replyToId: String?,
    val replyToRootId: String?,
    val replyToName: String?,
    val relayUrl: String,
    val name: String,
    val pictureUrl: String,
    val pubkey: String,
    val createdAt: Long,
    val content: String,
    val isLikedByMe: Boolean,
    val isRepostedByMe: Boolean,
    val referencePost: PostWithMeta? = null,
)
