package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kaiwolfram.nozzle.data.room.entity.PostEntity


@Dao
interface PostDao {

    /**
     * Sorted from newest to oldest
     */
    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE pubkey IN (SELECT contactPubkey FROM contact WHERE pubkey = :pubkey) " +
                "ORDER BY createdAt DESC " +
                "LIMIT :limit"
    )
    suspend fun getLatestFeed(pubkey: String, limit: Int = 100): List<PostEntity>

    /**
     * Sorted from newest to oldest
     */
    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE pubkey IN (:contactPubkeys) " +
                "ORDER BY createdAt DESC " +
                "LIMIT :limit"
    )
    suspend fun getLatestFeedOfCustomContacts(
        contactPubkeys: List<String>,
        limit: Int = 100
    ): List<PostEntity>

    /**
     * Sorted from newest to oldest
     */
    @Query(
        "SELECT * FROM (" +
                "SELECT * " +
                "FROM post " +
                "WHERE createdAt >= :since " +
                "AND pubkey IN (SELECT contactPubkey FROM contact WHERE pubkey = :pubkey) " +
                "ORDER BY createdAt " +
                "LIMIT :limit" +
                ") ORDER BY createdAt DESC"
    )
    suspend fun getFeedSince(pubkey: String, since: Long, limit: Int = 100): List<PostEntity>

    /**
     * Sorted from newest to oldest
     */
    @Query(
        "SELECT * FROM (" +
                "SELECT * " +
                "FROM post " +
                "WHERE createdAt >= :since " +
                "AND pubkey IN (:contactPubkeys) " +
                "ORDER BY createdAt " +
                "LIMIT :limit" +
                ") ORDER BY createdAt DESC"
    )
    suspend fun getFeedOfCustomContactsSince(
        contactPubkeys: List<String>,
        since: Long,
        limit: Int = 100
    ): List<PostEntity>

    @Query(
        "SELECT MAX(createdAt) " +
                "FROM post " +
                "WHERE pubkey IN (SELECT contactPubkey FROM contact WHERE pubkey = :pubkey) "
    )
    suspend fun getLatestTimestampOfFeed(pubkey: String): Long?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIfNotPresent(vararg post: PostEntity)
}
