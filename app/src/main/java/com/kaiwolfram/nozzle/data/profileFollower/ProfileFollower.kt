package com.kaiwolfram.nozzle.data.profileFollower

import android.util.Log
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.preferences.key.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.room.entity.ContactEntity

private const val TAG = "ProfileFollower"

// TODO: Synchronize Contact list in nostr and db
class ProfileFollower(
    private val nostrService: INostrService,
    private val pubkeyProvider: IPubkeyProvider,
    private val contactDao: ContactDao,
) : IProfileFollower {
    override suspend fun follow(pubkeyToFollow: String) {
        Log.i(TAG, "Follow $pubkeyToFollow")
        contactDao.insertContact(
            ContactEntity(
                pubkey = pubkeyProvider.getPubkey(),
                contactPubkey = pubkeyToFollow
            )
        )
        val contacts = contactDao.listContacts(pubkey = pubkeyProvider.getPubkey())
        nostrService.updateContactList(contacts = contacts)
    }

    override suspend fun unfollow(pubkeyToUnfollow: String) {
        Log.i(TAG, "Unfollow $pubkeyToUnfollow")
        contactDao.deleteContact(
            ContactEntity(
                pubkey = pubkeyProvider.getPubkey(),
                contactPubkey = pubkeyToUnfollow
            )
        )
        val contacts = contactDao.listContacts(pubkey = pubkeyProvider.getPubkey())
        nostrService.updateContactList(contacts = contacts)
    }

}
