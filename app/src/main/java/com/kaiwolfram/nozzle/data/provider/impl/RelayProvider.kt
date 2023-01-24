package com.kaiwolfram.nozzle.data.provider.impl

import android.util.Log
import com.kaiwolfram.nozzle.data.provider.IRelayProvider
import com.kaiwolfram.nozzle.data.room.dao.RelayDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val TAG = "RelayProvider"

class RelayProvider(private val relayDao: RelayDao) : IRelayProvider {
    init {
        val relays = listOf(
            "wss://nostr-pub.wellorder.net",
            "wss://nostr.onsats.org",
            "wss://nostr-relay.wlvs.space",
            "wss://nostr.bitcoiner.social",
            "wss://relay.damus.io",
            "wss://nostr.zebedee.cloud",
            "wss://nostr.fmt.wiz.biz",
            "wss://nostr.walletofsatoshi.com",
        )
        CoroutineScope(Dispatchers.IO).launch {
            relays.forEach {
                Log.i(TAG, "Insert relay $it")
                relayDao.insertOrIgnore(it)
            }
        }
    }

    override fun listRelays(): Flow<List<String>> {
        return relayDao.listRelays()
    }
}
