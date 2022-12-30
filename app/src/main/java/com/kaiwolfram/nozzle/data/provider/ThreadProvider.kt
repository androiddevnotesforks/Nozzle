package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.PostWithMeta

class ThreadProvider : IThreadProvider {
    override fun listPrevious(currentEventId: String): List<PostWithMeta> {
        TODO("Not yet implemented")
    }

    override fun getCurrent(currentEventId: String): PostWithMeta? {
        TODO("Not yet implemented")
    }

    override fun listReplies(currentEventId: String): List<PostWithMeta> {
        TODO("Not yet implemented")
    }
}
