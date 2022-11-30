package com.kaiwolfram.nozzle.data.preferences

import android.content.Context
import android.util.Log
import com.kaiwolfram.nozzle.data.utils.derivePublicKey
import com.kaiwolfram.nozzle.data.utils.generatePrivateKey
import com.kaiwolfram.nozzle.model.Profile

private const val TAG: String = "ProfilePreferences"

private object Variables {
    const val NAME: String = "name"
    const val PUBLIC_KEY: String = "public_key"
}

class ProfilePreferences(context: Context) {
    private val preferences = context.getSharedPreferences(
        PreferenceFileNames.PERSONAL_PROFILE,
        Context.MODE_PRIVATE
    )

    init {
        if (getPublicKey().isEmpty()) {
            val publicKey = derivePublicKey(generatePrivateKey())
            Log.i(TAG, "Setting initial public key $publicKey ")
            setPublicKey(publicKey)
        }
    }

    fun getName(): String {
        return preferences.getString(Variables.NAME, "") ?: ""
    }

    fun getPublicKey(): String {
        return preferences.getString(Variables.PUBLIC_KEY, "") ?: ""
    }

    private fun setPublicKey(publicKey: String) {
        if (publicKey.isBlank()) {
            Log.i(TAG, "Setting an empty public key is not allowed!")
            return
        }
        Log.i(TAG, "Set public key to $publicKey")
        preferences.edit()
            .putString(Variables.PUBLIC_KEY, publicKey)
            .apply()
    }

    fun setProfileValues(profile: Profile) {
        Log.i(TAG, "Set profile values $profile")
        preferences.edit()
            .putString(Variables.NAME, profile.name)
            .putString(Variables.PUBLIC_KEY, profile.pubKey)
            .apply()
    }
}
