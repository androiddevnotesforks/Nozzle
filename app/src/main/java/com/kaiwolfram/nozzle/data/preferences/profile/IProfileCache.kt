package com.kaiwolfram.nozzle.data.preferences.profile

interface IProfileCache : IProfileProvider {
    // TODO: set single metadata still needed?
    fun setName(name: String)
    fun setAbout(bio: String)
    fun setPicture(pictureUrl: String)
    fun setNip05(nip05: String)
    fun reset()
    fun setMeta(name: String, about: String, picture: String, nip05: String)
}
