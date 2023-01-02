package com.kaiwolfram.nozzle.model

import android.util.Log

private const val TAG = "InteractionCounts"

class InteractionCounts(
    private val numOfLikesPerPost: Map<String, Int>,
    private val numOfRepostsPerPost: Map<String, Int>,
    private val numOfRepliesPerPost: Map<String, Int>,
) {
    fun countLikes(postId: String): Int {
        val count = numOfLikesPerPost[postId]
        if (count == null) Log.i(TAG, "Can't count likes of post $postId")
        return count ?: 0
    }

    fun countReposts(postId: String): Int {
        val count = numOfRepostsPerPost[postId]
        if (count == null) Log.i(TAG, "Can't count reposts of post $postId")
        return count ?: 0
    }

    fun countReplies(postId: String): Int {
        val count = numOfRepliesPerPost[postId]
        if (count == null) Log.i(TAG, "Can't count replies of post $postId")
        return count ?: 0
    }
}
