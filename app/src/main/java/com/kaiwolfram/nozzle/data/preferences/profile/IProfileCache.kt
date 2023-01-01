package com.kaiwolfram.nozzle.data.preferences.profile

interface IProfileCache : IPersonalProfileProvider {
    fun reset()
    fun setMeta(name: String, about: String, picture: String, nip05: String)
}
