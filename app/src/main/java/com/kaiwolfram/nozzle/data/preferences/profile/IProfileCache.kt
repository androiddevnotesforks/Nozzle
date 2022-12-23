package com.kaiwolfram.nozzle.data.preferences.profile

interface IProfileCache : IProfileProvider {
    fun setName(name: String)
    fun setBio(bio: String)
    fun setPictureUrl(pictureUrl: String)
    fun reset()
}
