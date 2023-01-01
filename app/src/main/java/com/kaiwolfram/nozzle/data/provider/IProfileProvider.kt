package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.ProfileWithFollowerInfo

interface IProfileProvider {
    fun getProfile(pubkey: String): ProfileWithFollowerInfo?
}
