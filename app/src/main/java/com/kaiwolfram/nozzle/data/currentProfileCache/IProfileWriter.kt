package com.kaiwolfram.nozzle.data.currentProfileCache

interface IProfileWriter {
    fun setName(name: String)
    fun setBio(bio: String)
    fun setPictureUrl(pictureUrl: String)
    fun reset()
}
