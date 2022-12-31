package com.kaiwolfram.nozzle.data.preferences.profile

interface IProfileCache : IProfileProvider {
    fun reset()
    fun setMeta(name: String, about: String, picture: String, nip05: String)
}
