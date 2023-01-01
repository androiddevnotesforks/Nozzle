package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ReactionDao {
    @Query(
        "INSERT INTO reaction (eventId, pubkey) " +
                "VALUES (:eventId, :pubkey)"
    )
    fun like(eventId: String, pubkey: String)
}
