package com.kaiwolfram.nozzle.data.provider.impl

import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nozzle.data.provider.IProfileWithFollowerProvider
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.utils.hexToNpub
import com.kaiwolfram.nozzle.model.ProfileWithFollowerInfo

class ProfileWithFollowerProvider(
    private val pubkeyProvider: IPubkeyProvider,
    private val profileDao: ProfileDao,
    private val contactDao: ContactDao
) : IProfileWithFollowerProvider {

    override suspend fun getProfile(pubkey: String): ProfileWithFollowerInfo {
        val profile = profileDao.getProfile(pubkey) ?: return ProfileWithFollowerInfo(
            pubkey = pubkey,
            npub = hexToNpub(pubkey = pubkey),
            metadata = Metadata(),
            numOfFollowing = 0,
            numOfFollowers = 0,
            isOneself = pubkey == pubkeyProvider.getPubkey(),
            isFollowedByMe = false,
        )

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
