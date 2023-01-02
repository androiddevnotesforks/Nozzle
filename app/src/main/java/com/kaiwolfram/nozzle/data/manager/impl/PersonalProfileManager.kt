package com.kaiwolfram.nozzle.data.manager.impl

import com.kaiwolfram.nozzle.data.manager.IPersonalProfileManager
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao

class PersonalProfileManager(
    private val pubkeyProvider: IPubkeyProvider,
    private val profileDao: ProfileDao
) : IPersonalProfileManager {
    override fun setMeta(name: String, about: String, picture: String, nip05: String) {
        profileDao.updateMetadata(
            pubkey = getPubkey(),
            name = name,
            about = about,
            picture = picture,
            nip05 = nip05,
        )
    }

    override fun getName(): String {
        return profileDao.getName(getPubkey()).orEmpty()
    }

    override fun getPicture(): String {
        return profileDao.getName(getPubkey()).orEmpty()
    }

    override fun getAbout(): String {
        return profileDao.getAbout(getPubkey()).orEmpty()
    }

    override fun getNip05(): String {
        return profileDao.getNip05(getPubkey()).orEmpty()
    }

    override fun getPubkey() = pubkeyProvider.getPubkey()

    override fun getNpub() = pubkeyProvider.getNpub()
}
