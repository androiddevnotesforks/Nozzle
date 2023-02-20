package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.InteractionStats
import kotlinx.coroutines.flow.Flow

interface IInteractionStatsProvider {
    fun getStatsFlow(postIds: List<String>): Flow<InteractionStats>
}
