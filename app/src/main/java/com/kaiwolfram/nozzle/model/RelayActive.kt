package com.kaiwolfram.nozzle.model

data class RelayActive(
    val relayUrl: String,
    val isActive: Boolean,
    val count: Int = 0
)
