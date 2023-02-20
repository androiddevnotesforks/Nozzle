package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventRelayDao {
    @Query(
        "INSERT OR IGNORE INTO eventRelay (eventId, relayUrl) " +
                "VALUES (:eventId, :relayUrl)"
    )
    suspend fun insertOrIgnore(eventId: String, relayUrl: String)

    @MapInfo(keyColumn = "eventId", valueColumn = "relayUrl")
    @Query(
        "SELECT * " +
                "FROM eventRelay " +
                "WHERE eventId IN (:eventIds) "
    )
    fun getRelayMapFlow(eventIds: List<String>): Flow<Map<String, List<String>>>

    @Query(
        "SELECT DISTINCT(relayUrl) " +
                "FROM eventRelay " +
                "WHERE eventId IN " +
                "(SELECT id FROM post WHERE pubkey = :pubkey) "
    )
    fun listUsedRelaysFlow(pubkey: String): Flow<List<String>>
}
