package com.kaiwolfram.nozzle.data.utils

import com.kaiwolfram.nozzle.model.PostWithMeta

fun mapToLikedPost(toMap: PostWithMeta, id: String): PostWithMeta {
    return if (toMap.id == id) toMap.copy(isLikedByMe = true)
    else toMap
}
