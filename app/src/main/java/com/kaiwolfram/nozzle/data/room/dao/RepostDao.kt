package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface RepostDao {
    @Query(
        "INSERT OR IGNORE INTO repost (eventId, pubkey) " +
                "VALUES (:eventId, :pubkey)"
    )
    suspend fun repost(eventId: String, pubkey: String)
}
