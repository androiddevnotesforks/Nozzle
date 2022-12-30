package com.kaiwolfram.nozzle.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val pubkey: String,
    val name: String,
    val about: String,
    val picture: String,
    val nip05: String,
    val numOfFollowing: Int,
    val numOfFollowers: Int,
    val isFollowedByMe: Boolean,
)
