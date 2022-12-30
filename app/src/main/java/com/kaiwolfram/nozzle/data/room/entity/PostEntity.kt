package com.kaiwolfram.nozzle.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class PostEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val pubkey: String,
    val replyTo: String?,
    val createdAt: Long,
    val content: String,
)
