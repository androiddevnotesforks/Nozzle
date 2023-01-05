package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.*
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.model.RepostPreview


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
    suspend fun getLatestFeed(pubkey: String, limit: Int = 250): List<PostEntity>

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
        vararg contactPubkeys: String,
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
    suspend fun insertIfNotPresent(vararg post: PostEntity)

    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE replyToRootId = (SELECT replyToRootId FROM post WHERE id = :currentPostId) " +
                "AND replyToRootId IS NOT NULL"
    )
    suspend fun getWholeThread(currentPostId: String): List<PostEntity>

    @MapInfo(keyColumn = "id")
    @Query(
        "SELECT * " +
                "FROM post " +
                "JOIN profile ON post.pubkey = profile.pubkey " +
                "WHERE id IN (:postIds) "
    )
    suspend fun getRepostsPreviewMap(postIds: List<String>): Map<String, RepostPreview>

    @MapInfo(keyColumn = "repostedId", valueColumn = "repostCount")
    @Query(
        "SELECT repostedId, COUNT(*) AS repostCount " +
                "FROM post " +
                "WHERE repostedId IN (:postIds) " +
                "GROUP BY repostedId"
    )
    suspend fun getNumOfRepostsPerPost(postIds: List<String>): Map<String, Int>

    @MapInfo(keyColumn = "replyToId", valueColumn = "replyCount")
    @Query(
        "SELECT replyToId, COUNT(*) AS replyCount " +
                "FROM post " +
                "WHERE replyToId IN (:postIds) " +
                "GROUP BY replyToId"
    )
    suspend fun getNumOfRepliesPerPost(postIds: List<String>): Map<String, Int>

    @Query(
        "SELECT repostedId " +
                "FROM post " +
                "WHERE pubkey = :pubkey " +
                "AND repostedId IN (:postIds)"
    )
    suspend fun listRepostedByPubkey(pubkey: String, postIds: List<String>): List<String>
}
