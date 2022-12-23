package com.kaiwolfram.nozzle.data.currentProfileCache

import com.kaiwolfram.nozzle.data.preferences.key.IPubkeyReader

interface IProfileReader : IPubkeyReader {
    fun getName(): String
    fun getBio(): String
    fun getPictureUrl(): String
}
