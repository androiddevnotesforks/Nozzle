package com.kaiwolfram.nozzle.data.preferences.profile

import com.kaiwolfram.nozzle.data.preferences.key.IPubkeyProvider

interface IPersonalProfileProvider : IPubkeyProvider {
    fun getName(): String
    fun getBio(): String
    fun getPictureUrl(): String
    fun getNip05(): String
}
