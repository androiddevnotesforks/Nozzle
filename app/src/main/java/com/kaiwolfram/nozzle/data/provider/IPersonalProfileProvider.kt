package com.kaiwolfram.nozzle.data.provider

interface IPersonalProfileProvider : IPubkeyProvider {
    fun getName(): String
    fun getPicture(): String
    fun getAbout(): String
    fun getNip05(): String
}
