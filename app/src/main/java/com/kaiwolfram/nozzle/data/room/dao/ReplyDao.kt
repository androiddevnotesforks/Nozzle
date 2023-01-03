package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query

@Dao
interface ReplyDao {
    @Query(
        "INSERT OR IGNORE INTO reply (eventId, pubkey) " +
                "VALUES (:eventId, :pubkey)"
    )
    suspend fun reply(eventId: String, pubkey: String)

    @MapInfo(keyColumn = "eventId", valueColumn = "replyCount")
    @Query(
        "SELECT eventId, COUNT(*) AS replyCount " +
                "FROM reply " +
                "WHERE eventId IN (:postIds) " +
                "GROUP BY eventId"
    )
    suspend fun getNumOfRepliesPerPost(postIds: List<String>): Map<String, Int>
}
