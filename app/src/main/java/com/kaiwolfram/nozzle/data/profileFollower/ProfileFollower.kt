package com.kaiwolfram.nozzle.data.profileFollower

import android.util.Log
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.preferences.key.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.room.entity.ContactEntity

private const val TAG = "ProfileFollower"

class ProfileFollower(
    private val nostrService: INostrService,
    private val pubkeyProvider: IPubkeyProvider,
    private val contactDao: ContactDao,
) : IProfileFollower {
    override suspend fun follow(pubkeyToFollow: String) {
        Log.i(TAG, "Follow $pubkeyToFollow")
        contactDao.insert(
            ContactEntity(
                pubkey = pubkeyProvider.getPubkey(),
                contactPubkey = pubkeyToFollow
            )
        )
        nostrService.follow(pubkey = pubkeyToFollow)
    }

    override suspend fun unfollow(pubkeyToUnfollow: String) {
        Log.i(TAG, "Unfollow $pubkeyToUnfollow")
        contactDao.delete(
            ContactEntity(
                pubkey = pubkeyProvider.getPubkey(),
                contactPubkey = pubkeyToUnfollow
            )
        )
        nostrService.unfollow(pubkey = pubkeyToUnfollow)
    }

}
