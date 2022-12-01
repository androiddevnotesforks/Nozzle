package com.kaiwolfram.nozzle.data.preferences

import android.content.Context
import android.util.Log
import com.kaiwolfram.nozzle.data.derivePubkey
import com.kaiwolfram.nozzle.data.generatePrivateKey
import com.kaiwolfram.nozzle.data.nostr.NostrProfile

private const val TAG: String = "ProfilePreferences"

private object Variables {
    const val PUBKEY: String = "pubkey"
    const val NAME: String = "name"
    const val PICTURE_URL: String = "picture_url"
}

class ProfilePreferences(context: Context) {
    private val preferences = context.getSharedPreferences(
        PreferenceFileNames.PERSONAL_PROFILE,
        Context.MODE_PRIVATE
    )

    init {
        if (getPubkey().isEmpty()) {
            val pubkey = derivePubkey(generatePrivateKey())
            Log.i(TAG, "Setting initial public key $pubkey ")
            setPubkey(pubkey)
        }
    }

    fun getPubkey(): String {
        return preferences.getString(Variables.PUBKEY, "") ?: ""
    }

    fun getName(): String {
        return preferences.getString(Variables.NAME, "") ?: ""
    }

    fun getPictureUrl(): String {
        return preferences.getString(Variables.PICTURE_URL, "") ?: ""
    }

    fun setProfileValues(profile: NostrProfile) {
        Log.i(TAG, "Set profile values $profile")
        preferences.edit()
            .putString(Variables.PUBKEY, profile.pubkey)
            .putString(Variables.NAME, profile.name)
            .putString(Variables.PICTURE_URL, profile.picture)
            .apply()
    }

    private fun setPubkey(pubkey: String) {
        Log.i(TAG, "Set public key to $pubkey")
        preferences.edit()
            .putString(Variables.PUBKEY, pubkey)
            .apply()
    }
}
