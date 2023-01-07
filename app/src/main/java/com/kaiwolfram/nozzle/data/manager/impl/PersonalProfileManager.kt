package com.kaiwolfram.nozzle.data.manager.impl

import com.kaiwolfram.nostrclientkt.Metadata
import com.kaiwolfram.nozzle.data.manager.IPersonalProfileManager
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import kotlinx.coroutines.flow.Flow

class PersonalProfileManager(
    private val pubkeyProvider: IPubkeyProvider,
    private val profileDao: ProfileDao
) : IPersonalProfileManager {
    private var metadataFlow = profileDao.getMetadata(pubkeyProvider.getPubkey())

    override suspend fun setMeta(name: String, about: String, picture: String, nip05: String) {
        profileDao.updateMetadata(
            pubkey = getPubkey(),
            name = name,
            about = about,
            picture = picture,
            nip05 = nip05,
        )
    }

    override fun updateMetadata() {
        metadataFlow = profileDao.getMetadata(pubkeyProvider.getPubkey())
    }

    override fun getMetadata(): Flow<Metadata?> {
        return metadataFlow
    }

    override fun getPubkey() = pubkeyProvider.getPubkey()

    override fun getNpub() = pubkeyProvider.getNpub()
}
