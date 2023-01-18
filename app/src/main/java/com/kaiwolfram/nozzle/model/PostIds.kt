package com.kaiwolfram.nozzle.model

data class PostIds(
    val id: String,
    val replyToId: String?,
    val replyToRootId: String?,
) {
    companion object {
        fun fromId(id: String): PostIds {
            return PostIds(id = id, replyToId = null, replyToRootId = null)
        }
    }
}
