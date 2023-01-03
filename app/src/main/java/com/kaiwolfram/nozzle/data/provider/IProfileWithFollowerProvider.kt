package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.ProfileWithFollowerInfo

interface IProfileWithFollowerProvider {
    suspend fun getProfile(pubkey: String): ProfileWithFollowerInfo?
}
