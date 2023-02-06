package com.kaiwolfram.nozzle.data.room.entity

import androidx.room.Entity

@Entity(tableName = "userRelay", primaryKeys = ["pubkey", "relayUrl"])
data class UserRelayEntity(
    val pubkey: String,
    val relayUrl: String,
    val createdAt: Long,
    val type: Int,
)
