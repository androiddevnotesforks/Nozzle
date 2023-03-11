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
            "wss://nos.lol",
            "wss://nostr-pub.wellorder.net",
            "wss://nostr.einundzwanzig.space",
        )
        scope.launch {
            relays.forEach {
                Log.i(TAG, "Insert relay $it")
                relayDao.insertOrIgnore(it)
            }
        }
    }

    // TODO: Show only active relays.
    //  Initially nozzle defaults
    //  Then your nip65
    //  Then currently active in nostr client
    override fun listRelays(): List<String> {
        return relaysState.value
    }
}
