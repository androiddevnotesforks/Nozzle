package com.kaiwolfram.nozzle.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val pubkey: String,
    val name: String,
    val bio: String,
    val pictureUrl: String,
    val numOfFollowing: Int,
    val numOfFollowers: Int,
)
