package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.kaiwolfram.nozzle.data.room.entity.ProfileEntity

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE pubkey = :pubkey")
    suspend fun getProfile(pubkey: String): ProfileEntity?

    @Query(
        "UPDATE profile " +
                "SET name = :name, " +
                "about = :about, " +
                "picture = :picture, " +
                "nip05 = :nip05, " +
                "createdAt = :createdAt " +
                "WHERE pubkey = :pubkey"
    )
    suspend fun update(
        pubkey: String,
        name: String,
        about: String,
        picture: String,
        nip05: String,
        createdAt: Long,
    )

    @Query(
        "INSERT INTO profile (pubkey, name, about, picture, nip05, createdAt)" +
                "VALUES (:pubkey, :name, :about, :picture, :nip05, :createdAt)"
    )
    fun insert(
        pubkey: String,
        name: String,
        about: String,
        picture: String,
        nip05: String,
        createdAt: Long,
    )

    @Query(
        "DELETE FROM profile " +
                "WHERE pubkey = :pubkey AND createdAt < :createdAt"
    )
    fun deleteIfOutdated(pubkey: String, createdAt: Long)
}
