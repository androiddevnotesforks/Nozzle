package com.kaiwolfram.nozzle.data.preferences

interface PersonalProfileStorageReader {
    fun getPrivkey(): String
    fun getPubkey(): String
    fun getName(): String
    fun getBio(): String
    fun getPictureUrl(): String
}
