package com.kaiwolfram.nozzle.data.utils

import com.kaiwolfram.nozzle.model.PostThread
import com.kaiwolfram.nozzle.model.PostWithMeta

fun listReferencedPubkeys(thread: PostThread): List<String> {
    return listReferencedPubkeys(posts = collectPosts(thread))
}

fun listReferencedPostIds(thread: PostThread): List<String> {
    return listReferencedPostIds(posts = collectPosts(thread))
}

fun listPostIds(thread: PostThread): List<String> {
    return listPostIds(posts = collectPosts(thread))
}

fun collectPosts(thread: PostThread): List<PostWithMeta> {
    val posts = mutableListOf<PostWithMeta>()
    posts.addAll(thread.previous)
    thread.current?.let { posts.add(it) }
    posts.addAll(thread.replies)

    return posts
}
