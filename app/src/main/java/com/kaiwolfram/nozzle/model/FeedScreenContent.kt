package com.kaiwolfram.nozzle.model

import android.content.Context
import com.kaiwolfram.nozzle.R

sealed class FeedScreenContent(val feed: List<PostWithMeta>) {
    fun createWithNewFeed(newFeed: List<PostWithMeta>): FeedScreenContent {
        return when (this) {
            is HomeScreen -> HomeScreen(feed = newFeed)
        }
    }

    open fun getHeader(context: Context) = ""
}

class HomeScreen(feed: List<PostWithMeta>) : FeedScreenContent(feed = feed) {
    override fun getHeader(context: Context): String {
        return context.getString(R.string.home)
    }
}
