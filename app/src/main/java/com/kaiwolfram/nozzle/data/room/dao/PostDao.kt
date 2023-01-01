package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.kaiwolfram.nozzle.data.room.entity.PostEntity

// TODO: How to sort?

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
     * Sorted from oldest to newest
     */
    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE createdAt >= :since " +
                "AND pubkey IN (SELECT contactPubkey FROM contact WHERE pubkey = :pubkey) " +
                "ORDER BY createdAt " +
                "LIMIT :limit"
    )
    suspend fun getFeedSince(pubkey: String, since: Long, limit: Int = 100): List<PostEntity>

    /**
     * Sorted from oldest to newest
     */
    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE createdAt >= :since " +
                "AND pubkey IN (:contactPubkeys) " +
                "ORDER BY createdAt " +
                "LIMIT :limit"
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

    @Query(
        "INSERT INTO post (id, pubkey, replyTo, replyToRoot, content, createdAt) " +
                "VALUES (:id, :pubkey, :replyTo, :replyToRoot, :content, :createdAt) "
    )
    fun insert(
        id: String,
        pubkey: String,
        replyTo: String?,
        replyToRoot: String?,
        content: String,
        createdAt: Long,
    )
}
