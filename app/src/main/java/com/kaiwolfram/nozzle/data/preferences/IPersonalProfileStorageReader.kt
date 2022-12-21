package com.kaiwolfram.nozzle.data.preferences

interface IPersonalProfileStorageReader {
    fun getPrivkey(): String
    fun getPubkey(): String
    fun getName(): String
    fun getBio(): String
    fun getPictureUrl(): String
}
