package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.InteractionStats

interface IInteractionStatsProvider {
    fun getStats(postIds: List<String>): InteractionStats
}
