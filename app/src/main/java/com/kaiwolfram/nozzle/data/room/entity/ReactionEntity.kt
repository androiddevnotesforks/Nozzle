package com.kaiwolfram.nozzle.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reaction")
data class ReactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val eventId: String,
    val pubkey: String,
)
