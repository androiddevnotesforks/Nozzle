package com.kaiwolfram.nozzle.data.room.entity

import androidx.room.Entity

@Entity(tableName = "repost", primaryKeys = ["eventId", "pubkey"])
data class RepostEntity(
    val eventId: String,
    val pubkey: String,
)
