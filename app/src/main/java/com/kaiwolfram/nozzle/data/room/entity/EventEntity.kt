package com.kaiwolfram.nozzle.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class EventEntity(
    @PrimaryKey val id: String,
    val pubkey: String,
    val kind: Int,
    val createdAt: Long,
    val content: String
)
