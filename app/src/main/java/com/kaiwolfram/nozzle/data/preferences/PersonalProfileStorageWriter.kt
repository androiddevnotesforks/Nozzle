package com.kaiwolfram.nozzle.data.preferences

interface PersonalProfileStorageWriter {
    fun setPrivkey(privkey: String)
    fun setName(name: String)
    fun setBio(bio: String)
    fun setPictureUrl(pictureUrl: String)
    fun resetMetaData()
}
