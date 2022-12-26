package com.kaiwolfram.nozzle.data.preferences.profile

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.kaiwolfram.nozzle.data.preferences.PreferenceFileNames
import com.kaiwolfram.nozzle.data.preferences.key.IPubkeyProvider

private const val TAG: String = "ProfileCache"

private const val NAME: String = "name"
private const val BIO: String = "bio"
private const val PICTURE_URL: String = "picture_url"

class ProfileCache(
    private val pubkeyProvider: IPubkeyProvider,
    context: Context,
) : IProfileCache {
    private val preferences = context.getSharedPreferences(
        PreferenceFileNames.PROFILE_META,
        MODE_PRIVATE
    )

    override fun getPubkey() = pubkeyProvider.getPubkey()

    override fun getNpub() = pubkeyProvider.getNpub()

    override fun getName() = preferences.getString(NAME, "") ?: ""

    override fun getBio() = preferences.getString(BIO, "") ?: ""

    override fun getPictureUrl() = preferences.getString(PICTURE_URL, "") ?: ""

    override fun setName(name: String) {
        Log.i(TAG, "Set name $name")
        preferences.edit()
            .putString(NAME, name)
            .apply()
    }

    override fun setBio(bio: String) {
        Log.i(TAG, "Set bio $bio")
        preferences.edit()
            .putString(BIO, bio)
            .apply()
    }

    override fun setPictureUrl(pictureUrl: String) {
        Log.i(TAG, "Set pictureUrl $pictureUrl")
        preferences.edit()
            .putString(PICTURE_URL, pictureUrl)
            .apply()
    }

    override fun reset() {
        Log.i(TAG, "Reset values")
        preferences.edit().apply {
            putString(NAME, "")
            putString(BIO, "")
            putString(PICTURE_URL, "")
        }.apply()
    }
}
