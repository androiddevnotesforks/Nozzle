package com.kaiwolfram.nozzle.data.utils

import com.kaiwolfram.nozzle.model.PostWithMeta

fun listInvolvedPubkeys(posts: List<PostWithMeta>): List<String> {
    if (posts.isEmpty()) return listOf()

    val involvedPubKeys = mutableListOf<String>()
    for (post in posts) {
        involvedPubKeys.add(post.pubkey)
        post.replyToPubkey?.let { involvedPubKeys.add(it) }
        post.repost?.pubkey?.let { involvedPubKeys.add(it) }
    }

    return involvedPubKeys.distinct()
}

fun listReferencedPostIds(posts: List<PostWithMeta>): List<String> {
    if (posts.isEmpty()) return listOf()

    val referencedPostIds = mutableListOf<String>()
    for (post in posts) {
        post.replyToId?.let { referencedPostIds.add(it) }
        post.repost?.id?.let { referencedPostIds.add(it) }
    }

    return referencedPostIds.distinct()
}

fun listPostIds(posts: List<PostWithMeta>): List<String> {
    if (posts.isEmpty()) return listOf()

    return posts.map { it.id }
}
