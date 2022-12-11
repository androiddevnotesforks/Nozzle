package com.kaiwolfram.nozzle.data.preferences

import android.content.Context
import android.util.Log
import com.kaiwolfram.nozzle.data.derivePubkey
import com.kaiwolfram.nozzle.data.generatePrivkey
import com.kaiwolfram.nozzle.data.nostr.NostrProfile

private const val TAG: String = "ProfilePreferences"

private object Variables {
    const val PRIVKEY: String = "privkey"
    const val NAME: String = "name"
    const val BIO: String = "bio"
    const val PICTURE_URL: String = "picture_url"
}

class ProfilePreferences(context: Context) {
    private val preferences = context.getSharedPreferences(
        PreferenceFileNames.PERSONAL_PROFILE,
        Context.MODE_PRIVATE
    )

    init {
        if (getPrivkey().isEmpty()) {
            val privkey = generatePrivkey()
            Log.i(TAG, "Setting initial privkey $privkey ")
            setPrivkey(privkey)
        }
    }

    fun getPubkey(): String {
        return derivePubkey(getPrivkey())
    }

    fun getPrivkey(): String {
        return preferences.getString(Variables.PRIVKEY, "") ?: ""
    }

    fun setPrivkey(privkey: String) {
        preferences.edit()
            .putString(Variables.PRIVKEY, privkey)
            .apply()
    }

    fun getBio(): String {
        return preferences.getString(Variables.BIO, "") ?: ""
    }

    fun setBio(bio: String) {
        preferences.edit()
            .putString(Variables.BIO, bio)
            .apply()
    }

    fun getName(): String {
        return preferences.getString(Variables.NAME, "") ?: ""
    }

    fun setName(name: String) {
        preferences.edit()
            .putString(Variables.NAME, name)
            .apply()
    }

    fun getPictureUrl(): String {
        return preferences.getString(Variables.PICTURE_URL, "") ?: ""
    }

    fun setPictureUrl(pictureUrl: String) {
        preferences.edit()
            .putString(Variables.PICTURE_URL, pictureUrl)
            .apply()
    }

    fun setProfileValues(profile: NostrProfile) {
        Log.i(TAG, "Set name and picture URL $profile")
        preferences.edit()
            .putString(Variables.NAME, profile.name)
            .putString(Variables.PICTURE_URL, profile.picture)
            .apply()
    }
}
