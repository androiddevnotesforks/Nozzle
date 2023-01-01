package com.kaiwolfram.nozzle.data.utils

import com.kaiwolfram.nozzle.model.PostWithMeta

fun mapToLikedPost(toMap: PostWithMeta, id: String): PostWithMeta {
    return if (toMap.id == id) toMap.copy(isLikedByMe = true, numOfLikes = toMap.numOfLikes + 1)
    else toMap
}

fun mapToRepostedPost(toMap: PostWithMeta, id: String): PostWithMeta {
    return if (toMap.id == id) toMap.copy(
        isRepostedByMe = true,
        numOfReposts = toMap.numOfReposts + 1
    )
    else toMap
}
