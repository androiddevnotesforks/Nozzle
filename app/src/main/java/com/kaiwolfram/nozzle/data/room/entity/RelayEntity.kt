package com.kaiwolfram.nozzle.data.room.entity

import androidx.room.Entity

@Entity(tableName = "relay", primaryKeys = ["relayUrl"])
data class RelayEntity(
    val relayUrl: String,
)
