package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface EventRelayDao {
    @Query(
        "INSERT OR IGNORE INTO eventRelay (eventId, relayUrl) " +
                "VALUES (:eventId, :relayUrl)"
    )
    suspend fun insertOrIgnore(eventId: String, relayUrl: String)
}
