package com.kaiwolfram.nozzle.data.provider

interface IPersonalProfileProvider : IPubkeyProvider {
    fun getName(): String
    fun getBio(): String
    fun getPictureUrl(): String
    fun getNip05(): String
}
