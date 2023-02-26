package com.kaiwolfram.nozzle.data.preferences

import com.kaiwolfram.nozzle.model.FeedSettings

interface IFeedSettingsPreferences {
    fun getFeedSettings(): FeedSettings
    fun setFeedSettings(feedSettings: FeedSettings)
}
