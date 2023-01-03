package com.kaiwolfram.nozzle.data.manager.impl

import com.kaiwolfram.nostrclientkt.Metadata
import com.kaiwolfram.nozzle.data.manager.IPersonalProfileManager
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao

class PersonalProfileManager(
    private val pubkeyProvider: IPubkeyProvider,
    private val profileDao: ProfileDao
) : IPersonalProfileManager {
    override suspend fun setMeta(name: String, about: String, picture: String, nip05: String) {
        profileDao.updateMetadata(
            pubkey = getPubkey(),
            name = name,
            about = about,
            picture = picture,
            nip05 = nip05,
        )
    }

    override suspend fun getMetadata(): Metadata? {
        return profileDao.getMetadata(getPubkey())
    }

    override fun getPubkey() = pubkeyProvider.getPubkey()

    override fun getNpub() = pubkeyProvider.getNpub()
}
