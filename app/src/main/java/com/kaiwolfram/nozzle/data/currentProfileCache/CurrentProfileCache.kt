package com.kaiwolfram.nozzle.data.currentProfileCache

import android.util.Log
import com.kaiwolfram.nozzle.data.preferences.key.IPubkeyReader

private const val TAG: String = "CurrentProfileCache"

class CurrentProfileCache(
    private val currentPubkeyReader: IPubkeyReader,
) : IProfileCache {
    private var name = ""
    private var bio = ""
    private var pictureUrl = ""


    override fun getPubkey() = currentPubkeyReader.getPubkey()

    override fun getName() = name

    override fun getBio() = bio

    override fun getPictureUrl() = pictureUrl

    override fun setName(name: String) {
        Log.i(TAG, "Set name $name")
        this.name = name
    }

    override fun setBio(bio: String) {
        Log.i(TAG, "Set bio $bio")
        this.bio = bio
    }

    override fun setPictureUrl(pictureUrl: String) {
        Log.i(TAG, "Set pictureUrl $pictureUrl")
        this.pictureUrl = pictureUrl
    }

    override fun reset() {
        Log.i(TAG, "Reset values")
        setName("")
        setBio("")
        setPictureUrl("")
    }
}
