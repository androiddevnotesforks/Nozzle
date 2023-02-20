package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.ProfileWithAdditionalInfo
import kotlinx.coroutines.flow.Flow

interface IProfileWithAdditionalInfoProvider {
    fun getProfileFlow(pubkey: String): Flow<ProfileWithAdditionalInfo>
}
