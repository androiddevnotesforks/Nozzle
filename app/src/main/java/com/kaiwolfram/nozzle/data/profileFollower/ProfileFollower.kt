package com.kaiwolfram.nozzle.data.profileFollower

import android.util.Log
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.room.entity.ContactEntity

private const val TAG = "ProfileFollower"

class ProfileFollower(
    private val nostrService: INostrService,
    private val contactDao: ContactDao,
) : IProfileFollower {
    override suspend fun follow(pubkey: String, pubkeyToFollow: String) {
        Log.i(TAG, "$pubkey follows $pubkeyToFollow")
        contactDao.insert(ContactEntity(pubkey = pubkey, contactPubkey = pubkeyToFollow))
        nostrService.follow(pubkey = pubkeyToFollow)
    }

    override suspend fun unfollow(pubkey: String, pubkeyToUnfollow: String) {
        Log.i(TAG, "$pubkey unfollows $pubkeyToUnfollow")
        contactDao.delete(ContactEntity(pubkey = pubkey, contactPubkey = pubkeyToUnfollow))
        nostrService.unfollow(pubkey = pubkeyToUnfollow)
    }

}
