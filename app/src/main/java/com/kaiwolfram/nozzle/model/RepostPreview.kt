package com.kaiwolfram.nozzle.model

data class RepostPreview(
    val id: String,
    val pubkey: String,
    val content: String,
    val name: String,
    val picture: String,
) {
    fun toPostIds(): PostIds {
        return PostIds(id = id, replyToId = null, replyToRootId = null)
    }
}
