package com.kaiwolfram.nozzle.data.utils

import com.kaiwolfram.nozzle.model.PostWithMeta

fun listReferencedPubkeys(posts: Collection<PostWithMeta>): List<String> {
    if (posts.isEmpty()) return listOf()

    val referencedPubkeys = mutableListOf<String>()
    for (post in posts) {
        referencedPubkeys.add(post.pubkey)
        post.replyToPubkey?.let { referencedPubkeys.add(it) }
        post.repost?.pubkey?.let { referencedPubkeys.add(it) }
    }

    return referencedPubkeys.distinct()
}

fun listReferencedPostIds(posts: Collection<PostWithMeta>): List<String> {
    if (posts.isEmpty()) return listOf()

    val referencedPostIds = mutableListOf<String>()
    for (post in posts) {
        post.replyToId?.let { referencedPostIds.add(it) }
        post.repost?.id?.let { referencedPostIds.add(it) }
    }

    return referencedPostIds.distinct()
}
