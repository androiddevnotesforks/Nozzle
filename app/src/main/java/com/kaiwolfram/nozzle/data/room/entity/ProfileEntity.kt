package com.kaiwolfram.nozzle.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kaiwolfram.nostrclientkt.model.Metadata

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = false) val pubkey: String,
    val name: String,
    val about: String,
    val picture: String,
    val nip05: String,
    val createdAt: Long,
) {
    fun getMetadata(): Metadata {
        return Metadata(name, about, picture, nip05)
    }
}
