package com.kaiwolfram.nozzle.data.room.entity

import androidx.room.Entity

@Entity(tableName = "reply", primaryKeys = ["eventId", "pubkey"])
data class ReplyEntity(
    val eventId: String,
    val pubkey: String,
)
