package com.kaiwolfram.nozzle.data.provider.impl

import android.util.Log
import com.kaiwolfram.nozzle.data.provider.IRelayProvider
import com.kaiwolfram.nozzle.data.room.dao.RelayDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "RelayProvider"

class RelayProvider(private val relayDao: RelayDao) : IRelayProvider {
    private val scope = CoroutineScope(context = Dispatchers.Default)
    private var relaysState = relayDao.listRelays().stateIn(
        scope, SharingStarted.Eagerly, listOf()
    )

    init {
        val relays = listOf(
            "wss://nostr.zebedee.cloud",
            "wss://nostr.fmt.wiz.biz",
            "wss://nostr.einundzwanzig.space",
            "wss://relay.snort.social",
            "wss://nostr-pub.wellorder.net",
        )
        scope.launch {
            relays.forEach {
                Log.i(TAG, "Insert relay $it")
                relayDao.insertOrIgnore(it)
            }
        }
    }

    override fun listRelays(): List<String> {
        return relaysState.value
    }
}
