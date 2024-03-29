package com.kaiwolfram.nozzle.data.provider.impl

import android.util.Log
import com.kaiwolfram.nozzle.data.provider.IContactListProvider
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

private const val TAG = "ContactListProvider"

class ContactListProvider(
    private val pubkeyProvider: IPubkeyProvider,
    private val contactDao: ContactDao
) : IContactListProvider {
    private val scope = CoroutineScope(context = Dispatchers.Default)

    // TODO: Determine current pubkey by db table. PubkeyProvider should not be needed
    private var personalPubkey = pubkeyProvider.getPubkey()
    private var personalContactListState = contactDao.listContactPubkeysFlow(personalPubkey)
        .stateIn(
            scope, SharingStarted.Eagerly, listOf()
        )

    override fun listPersonalContactPubkeys(): List<String> {
        // TODO: Obsolete this check. See TODO above
        if (personalPubkey != pubkeyProvider.getPubkey()) {
            personalPubkey = pubkeyProvider.getPubkey()
            personalContactListState = contactDao.listContactPubkeysFlow(personalPubkey)
                .stateIn(
                    scope, SharingStarted.Eagerly, listOf()
                )
        }
        Log.i(TAG, "Return ${personalContactListState.value.size} pubkeys")
        return personalContactListState.value
    }
}
