package com.kaiwolfram.nozzle.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repost")
data class RepostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val eventId: Long,
    val pubkey: String,
)
