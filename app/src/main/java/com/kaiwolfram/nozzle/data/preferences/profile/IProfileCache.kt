package com.kaiwolfram.nozzle.data.preferences.profile

interface IProfileCache : IProfileProvider {
    fun setName(name: String)
    fun setBio(bio: String)
    fun setPictureUrl(pictureUrl: String)
    fun setNip05(nip05: String)
    fun reset()
}
