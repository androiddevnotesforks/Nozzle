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
                "WHERE ((:isReplies AND replyToId IS NOT NULL) OR (:isPosts AND replyToId IS NULL))" +
                "AND (:authorPubkeys IS NULL OR pubkey IN (:authorPubkeys)) " +
                "AND (:relays IS NULL OR id IN (SELECT DISTINCT eventId FROM eventRelay WHERE relayUrl IN (:relays))) " +
                "AND createdAt <= :until " +
                "ORDER BY createdAt DESC " +
                "LIMIT :limit"
    )
    suspend fun getFeed(
        isPosts: Boolean,
        isReplies: Boolean,
        authorPubkeys: List<String>?,
        relays: List<String>?,
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

    @Query(
        "SELECT pubkey " +
                "FROM post " +
                "WHERE id IN (:postIds) "
    )
    suspend fun listAuthorPubkeys(postIds: List<String>): List<String>
}
