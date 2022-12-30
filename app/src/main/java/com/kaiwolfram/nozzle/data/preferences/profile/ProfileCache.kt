package com.kaiwolfram.nozzle.data.preferences.profile

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.kaiwolfram.nozzle.data.preferences.PreferenceFileNames
import com.kaiwolfram.nozzle.data.preferences.key.IPubkeyProvider

private const val TAG: String = "ProfileCache"

private const val NAME: String = "name"
private const val ABOUT: String = "about"
private const val PICTURE: String = "picture"
private const val NIP05: String = "nip05"

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

    override fun getBio() = preferences.getString(ABOUT, "") ?: ""

    override fun getPictureUrl() = preferences.getString(PICTURE, "") ?: ""

    override fun getNip05() = preferences.getString(NIP05, "") ?: ""

    override fun setName(name: String) {
        Log.i(TAG, "Set name $name")
        preferences.edit()
            .putString(NAME, name)
            .apply()
    }

    override fun setAbout(bio: String) {
        Log.i(TAG, "Set bio $bio")
        preferences.edit()
            .putString(ABOUT, bio)
            .apply()
    }

    override fun setPicture(pictureUrl: String) {
        Log.i(TAG, "Set pictureUrl $pictureUrl")
        preferences.edit()
            .putString(PICTURE, pictureUrl)
            .apply()
    }

    override fun setNip05(nip05: String) {
        Log.i(TAG, "Set nip05 $nip05")
        preferences.edit()
            .putString(NIP05, nip05)
            .apply()
    }

    override fun reset() {
        Log.i(TAG, "Reset values")
        preferences.edit().apply {
            putString(NAME, "")
            putString(ABOUT, "")
            putString(PICTURE, "")
        }.apply()
    }

    override fun setMeta(name: String, about: String, picture: String, nip05: String) {
        Log.i(TAG, "Set name $name, about $about, picture $picture and nip05 $nip05")
        preferences.edit()
            .putString(NAME, name)
            .putString(ABOUT, about)
            .putString(PICTURE, picture)
            .putString(NIP05, nip05)
            .apply()
    }
}
