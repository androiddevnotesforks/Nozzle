package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.*
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.model.RepostPreview
import kotlinx.coroutines.flow.Flow


@Dao
interface PostDao {

    /**
     * Sorted from newest to oldest
     */
    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE ((:isReplies AND replyToId IS NOT NULL) OR (:isPosts AND replyToId IS NULL)) " +
                "AND pubkey IN (:authorPubkeys) " +
                "AND id IN (SELECT DISTINCT eventId FROM eventRelay WHERE relayUrl IN (:relays)) " +
                "AND createdAt < :until " +
                "ORDER BY createdAt DESC " +
                "LIMIT :limit"
    )
    suspend fun getAuthoredFeedByRelays(
        isPosts: Boolean,
        isReplies: Boolean,
        authorPubkeys: List<String>,
        relays: List<String>,
        until: Long,
        limit: Int,
    ): List<PostEntity>

    /**
     * Sorted from newest to oldest
     */
    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE ((:isReplies AND replyToId IS NOT NULL) OR (:isPosts AND replyToId IS NULL)) " +
                "AND pubkey IN (:authorPubkeys) " +
                "AND createdAt < :until " +
                "ORDER BY createdAt DESC " +
                "LIMIT :limit"
    )
    suspend fun getAuthoredFeed(
        isPosts: Boolean,
        isReplies: Boolean,
        authorPubkeys: List<String>,
        until: Long,
        limit: Int,
    ): List<PostEntity>

    /**
     * Sorted from newest to oldest
     */
    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE ((:isReplies AND replyToId IS NOT NULL) OR (:isPosts AND replyToId IS NULL)) " +
                "AND id IN (SELECT DISTINCT eventId FROM eventRelay WHERE relayUrl IN (:relays)) " +
                "AND createdAt < :until " +
                "ORDER BY createdAt DESC " +
                "LIMIT :limit"
    )
    suspend fun getGlobalFeedByRelays(
        isPosts: Boolean,
        isReplies: Boolean,
        relays: List<String>,
        until: Long,
        limit: Int,
    ): List<PostEntity>

    /**
     * Sorted from newest to oldest
     */
    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE ((:isReplies AND replyToId IS NOT NULL) OR (:isPosts AND replyToId IS NULL)) " +
                "AND createdAt < :until " +
                "ORDER BY createdAt DESC " +
                "LIMIT :limit"
    )
    suspend fun getGlobalFeed(
        isPosts: Boolean,
        isReplies: Boolean,
        until: Long,
        limit: Int,
    ): List<PostEntity>


    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE id = :id "
    )
    suspend fun getPost(id: String): PostEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNotPresent(vararg post: PostEntity)

    @Query(
        "SELECT * " +
                "FROM post " +
                "WHERE replyToId = :currentPostId " + // All replies to current post
                "OR id = :currentPostId " + // Current post
                "OR (:replyToId IS NOT NULL AND id = :replyToId) " // Direct parent
    )
    suspend fun getThreadEnd(currentPostId: String, replyToId: String?): List<PostEntity>

    @MapInfo(keyColumn = "id")
    @Query(
        "SELECT id, post.pubkey, content, name, picture, post.createdAt " +
                "FROM post " +
                "JOIN profile ON post.pubkey = profile.pubkey " +
                "WHERE id IN (:postIds) "
    )
    fun getRepostsPreviewMapFlow(postIds: List<String>): Flow<Map<String, RepostPreview>>

    @MapInfo(keyColumn = "repostedId", valueColumn = "repostCount")
    @Query(
        "SELECT repostedId, COUNT(*) AS repostCount " +
                "FROM post " +
                "WHERE repostedId IN (:postIds) " +
                "GROUP BY repostedId"
    )
    fun getNumOfRepostsPerPostFlow(postIds: List<String>): Flow<Map<String, Int>>

    @MapInfo(keyColumn = "replyToId", valueColumn = "replyCount")
    @Query(
        "SELECT replyToId, COUNT(*) AS replyCount " +
                "FROM post " +
                "WHERE replyToId IN (:postIds) " +
                "GROUP BY replyToId"
    )
    fun getNumOfRepliesPerPostFlow(postIds: List<String>): Flow<Map<String, Int>>

    @Query(
        "SELECT repostedId " +
                "FROM post " +
                "WHERE pubkey = :pubkey " +
                "AND repostedId IN (:postIds)"
    )
    fun listRepostedByPubkeyFlow(pubkey: String, postIds: List<String>): Flow<List<String>>

    @Query(
        "SELECT pubkey " +
                "FROM post " +
                "WHERE id IN (:postIds) "
    )
    suspend fun listAuthorPubkeys(postIds: List<String>): List<String>
}
