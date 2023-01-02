package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.InteractionCounts

interface IInteractionCountProvider {
    fun getCounts(postIds: List<String>): InteractionCounts
}
