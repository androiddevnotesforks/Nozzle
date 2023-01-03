package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query

@Dao
interface ReactionDao {
    @Query(
        "INSERT OR IGNORE INTO reaction (eventId, pubkey) " +
                "VALUES (:eventId, :pubkey)"
    )
    suspend fun like(eventId: String, pubkey: String)

    @MapInfo(keyColumn = "eventId", valueColumn = "reactionCount")
    @Query(
        "SELECT eventId, COUNT(*) AS reactionCount " +
                "FROM reaction " +
                "WHERE eventId IN (:postIds) " +
                "GROUP BY eventId"
    )
    suspend fun getNumOfLikesPerPost(postIds: List<String>): Map<String, Int>

    @Query(
        "SELECT eventId " +
                "FROM reaction " +
                "WHERE pubkey = :pubkey " +
                "AND eventId IN (:postIds)"
    )
    suspend fun listLikedBy(pubkey: String, postIds: List<String>): List<String>
}
