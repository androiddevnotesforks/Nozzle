package com.kaiwolfram.nozzle.ui.app.navigation

object NozzleRoute {
    const val PROFILE_FULL = "profile/{${Identifier.PUBKEY}}"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val FEED = "feed"
    const val SEARCH = "search"
    const val KEYS = "keys"
    const val THREAD_FULL = "thread/{${Identifier.POST_ID}}" +
            "?${Identifier.REPLY_TO_ID}={${Identifier.REPLY_TO_ID}}" +
            "?${Identifier.REPLY_TO_ROOT_ID}={${Identifier.REPLY_TO_ROOT_ID}}"
    const val THREAD = "thread"
    const val REPLY = "reply"
    const val POST = "post"
}

object Identifier {
    const val PUBKEY = "pubkey"
    const val POST_ID = "postId"
    const val REPLY_TO_ID = "replyToId"
    const val REPLY_TO_ROOT_ID = "replyToId"
}
