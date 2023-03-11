package com.kaiwolfram.nozzle.data.preferences

import android.content.Context
import android.util.Log
import com.kaiwolfram.nostrclientkt.model.Autopilot
import com.kaiwolfram.nozzle.data.PreferenceFileNames
import com.kaiwolfram.nozzle.model.Contacts
import com.kaiwolfram.nozzle.model.Everyone
import com.kaiwolfram.nozzle.model.FeedSettings

private const val TAG: String = "NozzlePreferences"

const val FEED_SETTINGS_IS_CONTACTS_ONLY: String = "feed_settings_is_contacts_only"
const val FEED_SETTINGS_IS_POSTS: String = "feed_settings_is_posts"
const val FEED_SETTINGS_IS_REPLIES: String = "feed_settings_is_replies"

class NozzlePreferences(context: Context) : IFeedSettingsPreferences {
    private val preferences =
        context.getSharedPreferences(PreferenceFileNames.NOZZLE, Context.MODE_PRIVATE)

    init {
        Log.i(TAG, "Initialize $TAG")
    }

    override fun getFeedSettings(): FeedSettings {
        val isContactsOnly = preferences.getBoolean(FEED_SETTINGS_IS_CONTACTS_ONLY, true)
        val isPosts = preferences.getBoolean(FEED_SETTINGS_IS_POSTS, true)
        val isReplies = preferences.getBoolean(FEED_SETTINGS_IS_REPLIES, true)
        return FeedSettings(
            isPosts = isPosts,
            isReplies = isReplies,
            authorSelection = if (isContactsOnly) Contacts else Everyone,
            relaySelection = Autopilot // Default
        )
    }

    override fun setFeedSettings(feedSettings: FeedSettings) {
        preferences.edit()
            .putBoolean(
                FEED_SETTINGS_IS_CONTACTS_ONLY,
                feedSettings.authorSelection.isContactsOnly()
            )
            .putBoolean(FEED_SETTINGS_IS_POSTS, feedSettings.isPosts)
            .putBoolean(FEED_SETTINGS_IS_REPLIES, feedSettings.isReplies)
            .apply()
    }
}
