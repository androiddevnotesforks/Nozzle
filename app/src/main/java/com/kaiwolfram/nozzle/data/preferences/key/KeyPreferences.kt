package com.kaiwolfram.nozzle.data.preferences.key

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.kaiwolfram.nozzle.data.preferences.PreferenceFileNames
import com.kaiwolfram.nozzle.data.utils.derivePubkey
import com.kaiwolfram.nozzle.data.utils.generatePrivkey
import com.kaiwolfram.nozzle.data.utils.hexToNpub


private const val TAG: String = "KeyPreferences"

private const val PRIVKEY: String = "privkey"


class KeyPreferences(context: Context) : IKeyManager {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    private val preferences = EncryptedSharedPreferences.create(
        context,
        PreferenceFileNames.KEYS,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private var pubkey: String

    init {
        Log.i(TAG, "Initialize KeyPreferences")
        var privkey = getPrivkey()
        if (privkey.isEmpty()) {
            privkey = generatePrivkey()
            Log.i(TAG, "Setting initial privkey $privkey")
            setPrivkey(privkey)
        }
        pubkey = derivePubkey(privkey)
    }

    override fun getPubkey() = pubkey

    override fun getNpub() = hexToNpub(pubkey)

    override fun getPrivkey() = preferences.getString(PRIVKEY, "") ?: ""

    override fun setPrivkey(privkey: String) {
        pubkey = derivePubkey(privkey)
        Log.i(TAG, "Setting privkey and derived pubkey $pubkey")
        preferences.edit()
            .putString(PRIVKEY, privkey)
            .apply()
    }

}
