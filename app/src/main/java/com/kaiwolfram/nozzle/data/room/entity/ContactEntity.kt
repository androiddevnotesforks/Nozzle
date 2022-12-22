package com.kaiwolfram.nozzle.data.room.entity

import androidx.room.Entity

@Entity(tableName = "contact", primaryKeys = ["pubkey", "contactPubkey"])
data class ContactEntity(
    val pubkey: String,
    val contactPubkey: String,
)
