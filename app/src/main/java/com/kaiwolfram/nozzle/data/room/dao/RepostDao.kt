package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import com.kaiwolfram.nozzle.model.RepostPreview

@Dao
interface RepostDao {
    @Query(
        "INSERT OR IGNORE INTO repost (eventId, pubkey) " +
                "VALUES (:eventId, :pubkey)"
    )
    suspend fun repost(eventId: String, pubkey: String)

    @MapInfo(keyColumn = "eventId", valueColumn = "repostCount")
    @Query(
        "SELECT eventId, COUNT(*) AS repostCount " +
                "FROM repost " +
                "WHERE eventId IN (:postIds) " +
                "GROUP BY eventId"
    )
    suspend fun getNumOfRepostsPerPost(postIds: List<String>): Map<String, Int>

    @Query(
        "SELECT eventId " +
                "FROM repost " +
                "WHERE pubkey = :pubkey " +
                "AND eventId IN (:postIds)"
    )
    suspend fun listRepostedByMe(pubkey: String, postIds: List<String>): List<String>

    @MapInfo(keyColumn = "id")
    @Query(
        "SELECT * " +
                "FROM post " +
                "JOIN profile ON post.pubkey = profile.pubkey " +
                "WHERE id IN (:postIds) "
    )
    suspend fun getRepostsMap(postIds: List<String>): Map<String, RepostPreview>
}
