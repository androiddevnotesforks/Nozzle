package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.kaiwolfram.nozzle.data.room.entity.ProfileEntity

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE pubkey = :pubkey")
    suspend fun getProfile(pubkey: String): ProfileEntity?

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(vararg profiles: ProfileEntity)
//
    @Query(
        "UPDATE profile " +
                "SET name = :name, " +
                "about = :about, " +
                "picture = :picture, " +
                "nip05 = :nip05 " +
                "WHERE pubkey = :pubkey"
    )
    suspend fun updateMetadata(
        pubkey: String,
        name: String,
        about: String,
        picture: String,
        nip05: String
    )
//
//    @Query("SELECT name, picture FROM profile WHERE pubkey = :pubkey")
//    suspend fun getName(pubkey: String): NameAndPictureUrl
}
