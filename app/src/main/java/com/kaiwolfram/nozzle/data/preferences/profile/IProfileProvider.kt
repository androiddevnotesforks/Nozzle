package com.kaiwolfram.nozzle.data.preferences.profile

import com.kaiwolfram.nozzle.data.preferences.key.IPubkeyProvider

interface IProfileProvider : IPubkeyProvider {
    fun getName(): String
    fun getBio(): String
    fun getPictureUrl(): String
}
