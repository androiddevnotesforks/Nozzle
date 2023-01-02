package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kaiwolfram.nozzle.data.room.entity.ProfileEntity

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE pubkey = :pubkey")
    fun getProfile(pubkey: String): ProfileEntity?

    @Query("SELECT name FROM profile WHERE pubkey = :pubkey")
    fun getName(pubkey: String): String?

    @Query("SELECT picture FROM profile WHERE pubkey = :pubkey")
    fun getPicture(pubkey: String): String?

    @Query("SELECT about FROM profile WHERE pubkey = :pubkey")
    fun getAbout(pubkey: String): String?

    @Query("SELECT nip05 FROM profile WHERE pubkey = :pubkey")
    fun getNip05(pubkey: String): String?

    @Query(
        "UPDATE profile " +
                "SET name = :name, " +
                "about = :about, " +
                "picture = :picture, " +
                "nip05 = :nip05 " +
                "WHERE pubkey = :pubkey"
    )
    fun updateMetadata(
        pubkey: String,
        name: String,
        about: String,
        picture: String,
        nip05: String,
    )

    // TODO: Replace if createdAt is larger
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceIfNewer(vararg profile: ProfileEntity)

    @Query(
        "DELETE FROM profile " +
                "WHERE pubkey = :pubkey AND createdAt < :createdAt"
    )
    fun deleteIfOutdated(pubkey: String, createdAt: Long)
}
