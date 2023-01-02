package com.kaiwolfram.nozzle.model

import android.util.Log

private const val TAG = "InteractionStats"

class InteractionStats(
    private val numOfLikesPerPost: Map<String, Int>,
    private val numOfRepostsPerPost: Map<String, Int>,
    private val numOfRepliesPerPost: Map<String, Int>,
    private val likedByMe: List<String>,
    private val repostedByMe: List<String>,
) {
    fun getNumOfLikes(postId: String): Int {
        val count = numOfLikesPerPost[postId]
        if (count == null) Log.i(TAG, "Can't count likes of post $postId")
        return count ?: 0
    }

    fun getNumOfReposts(postId: String): Int {
        val count = numOfRepostsPerPost[postId]
        if (count == null) Log.i(TAG, "Can't count reposts of post $postId")
        return count ?: 0
    }

    fun getNumOfReplies(postId: String): Int {
        val count = numOfRepliesPerPost[postId]
        if (count == null) Log.i(TAG, "Can't count replies of post $postId")
        return count ?: 0
    }

    fun isLikedByMe(postId: String) = likedByMe.contains(postId)

    fun isRepostedByMe(postId: String) = repostedByMe.contains(postId)
}
