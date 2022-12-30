package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kaiwolfram.nozzle.data.room.entity.PostEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM event WHERE pubkey = :pubkey")
    suspend fun listEventsFromPubkey(pubkey: String): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(events: List<PostEntity>)
}
