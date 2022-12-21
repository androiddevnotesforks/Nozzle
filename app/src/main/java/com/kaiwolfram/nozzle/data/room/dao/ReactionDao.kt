package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ReactionDao {
    @Query(
        "INSERT INTO reaction (eventId, pubkey, content) " +
                "VALUES (:eventId, :pubkey, '+')"
    )
    suspend fun like(eventId: String, pubkey: String)
}
