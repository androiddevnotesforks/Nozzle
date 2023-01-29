package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query

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
    suspend fun getRelayMap(eventIds: List<String>): Map<String, List<String>>

    @Query(
        "SELECT COUNT(DISTINCT(relayUrl)) " +
                "FROM eventRelay " +
                "WHERE eventId IN " +
                "(SELECT id FROM post WHERE pubkey = :pubkey) "
    )
    suspend fun getNumberOfUsedRelays(pubkey: String): Int
}
