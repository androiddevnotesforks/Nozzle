package com.kaiwolfram.nozzle.data.utils

import com.kaiwolfram.nozzle.model.PostWithMeta

fun listInvolvedPubkeys(posts: List<PostWithMeta>): Set<String> {
    if (posts.isEmpty()) return setOf()

    val involvedPubKeys = mutableSetOf<String>()
    for (post in posts) {
        involvedPubKeys.add(post.pubkey)
        post.replyToPubkey?.let { involvedPubKeys.add(it) }
        post.repost?.pubkey?.let { involvedPubKeys.add(it) }
    }

    return involvedPubKeys
}

fun listReferencedPostIds(posts: List<PostWithMeta>): Set<String> {
    if (posts.isEmpty()) return setOf()

    val referencedPostIds = mutableSetOf<String>()
    for (post in posts) {
        post.replyToId?.let { referencedPostIds.add(it) }
        post.repost?.id?.let { referencedPostIds.add(it) }
    }

    return referencedPostIds
}
