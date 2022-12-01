package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kaiwolfram.nozzle.data.room.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM event WHERE pubkey = :pubkey")
    suspend fun listEventsFromPubkey(pubkey: String): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(events: List<EventEntity>)
}
