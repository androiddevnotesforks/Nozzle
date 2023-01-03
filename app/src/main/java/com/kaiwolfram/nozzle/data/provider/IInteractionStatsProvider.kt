package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.InteractionStats

interface IInteractionStatsProvider {
    suspend fun getStats(postIds: List<String>): InteractionStats
}
