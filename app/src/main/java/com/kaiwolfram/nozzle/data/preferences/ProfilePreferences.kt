package com.kaiwolfram.nozzle.data.preferences

import android.content.Context
import android.util.Log
import com.kaiwolfram.nozzle.data.derivePubkey
import com.kaiwolfram.nozzle.data.generatePrivkey

private const val TAG: String = "ProfilePreferences"

private object Variables {
    const val PRIVKEY: String = "privkey"
    const val NAME: String = "name"
    const val BIO: String = "bio"
    const val PICTURE_URL: String = "picture_url"
}

class ProfilePreferences(context: Context) : PersonalProfileStorage {
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

    override fun getPubkey(): String {
        return derivePubkey(getPrivkey())
    }

    override fun getPrivkey(): String {
        return preferences.getString(Variables.PRIVKEY, "") ?: ""
    }

    override fun setPrivkey(privkey: String) {
        preferences.edit()
            .putString(Variables.PRIVKEY, privkey)
            .apply()
    }

    override fun getBio(): String {
        return preferences.getString(Variables.BIO, "") ?: ""
    }

    override fun setBio(bio: String) {
        preferences.edit()
            .putString(Variables.BIO, bio)
            .apply()
    }

    override fun getName(): String {
        return preferences.getString(Variables.NAME, "") ?: ""
    }

    override fun setName(name: String) {
        preferences.edit()
            .putString(Variables.NAME, name)
            .apply()
    }

    override fun getPictureUrl(): String {
        return preferences.getString(Variables.PICTURE_URL, "") ?: ""
    }

    override fun setPictureUrl(pictureUrl: String) {
        preferences.edit()
            .putString(Variables.PICTURE_URL, pictureUrl)
            .apply()
    }

    override fun resetMetaData() {
        preferences.edit()
            .putString(Variables.NAME, "")
            .putString(Variables.BIO, "")
            .putString(Variables.PICTURE_URL, "")
            .apply()
    }
}
