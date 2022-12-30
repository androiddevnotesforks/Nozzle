package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.PostWithMeta

interface IThreadProvider {
    fun listPrevious(currentEventId: String): List<PostWithMeta>
    fun getCurrent(currentEventId: String): PostWithMeta?
    fun listReplies(currentEventId: String): List<PostWithMeta>
}
