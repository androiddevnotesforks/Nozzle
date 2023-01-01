package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.data.preferences.key.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.utils.hexToNpub
import com.kaiwolfram.nozzle.model.ProfileWithFollowerInfo

class ProfileProvider(
    private val pubkeyProvider: IPubkeyProvider,
    private val profileDao: ProfileDao,
    private val contactDao: ContactDao
) : IProfileProvider {

    override fun getProfile(pubkey: String): ProfileWithFollowerInfo? {
        val profile = profileDao.getProfile(pubkey) ?: return null
        val numOfFollowing = contactDao.getNumberOfFollowing(pubkey)
        val numOfFollowers = contactDao.getNumberOfFollowers(pubkey)
        val isFollowedByMe = contactDao.isFollowed(
            pubkey = pubkeyProvider.getPubkey(),
            contactPubkey = pubkey
        )

        return ProfileWithFollowerInfo(
            pubkey = pubkey,
            npub = hexToNpub(pubkey = pubkey),
            metadata = profile.getMetadata(),
            numOfFollowing = numOfFollowing,
            numOfFollowers = numOfFollowers,
            isOneself = pubkey == pubkeyProvider.getPubkey(),
            isFollowedByMe = isFollowedByMe,
        )
    }
}
