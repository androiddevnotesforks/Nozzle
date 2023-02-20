package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.*
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nozzle.data.room.entity.ProfileEntity
import com.kaiwolfram.nozzle.model.NameAndPicture
import com.kaiwolfram.nozzle.model.NameAndPubkey
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE pubkey = :pubkey")
    suspend fun getProfile(pubkey: String): ProfileEntity?

    @Query("SELECT * FROM profile WHERE pubkey = :pubkey")
    fun getProfileFlow(pubkey: String): Flow<ProfileEntity?>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM profile WHERE pubkey = :pubkey")
    fun getMetadata(pubkey: String): Flow<Metadata?>

    @Query(
        "UPDATE profile " +
                "SET name = :name, " +
                "about = :about, " +
                "picture = :picture, " +
                "nip05 = :nip05, " +
                "lud16 = :lud16 " +
                "WHERE pubkey = :pubkey"
    )
    suspend fun updateMetadata(
        pubkey: String,
        name: String,
        about: String,
        picture: String,
        nip05: String,
        lud16: String,
    )

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(vararg profile: ProfileEntity)

    @Query(
        "DELETE FROM profile " +
                "WHERE pubkey = :pubkey AND createdAt < :createdAt"
    )
    suspend fun deleteIfOutdated(pubkey: String, createdAt: Long)

    @RewriteQueriesToDropUnusedColumns
    @MapInfo(keyColumn = "pubkey")
    @Query(
        "SELECT * " +
                "FROM profile " +
                "WHERE pubkey IN (:pubkeys) "
    )
    fun getNamesAndPicturesMapFlow(pubkeys: List<String>): Flow<Map<String, NameAndPicture>>

    @MapInfo(keyColumn = "postId")
    @Query(
        "SELECT id AS postId, name, profile.pubkey " +
                "FROM profile " +
                "JOIN post ON post.pubkey = profile.pubkey " +
                "WHERE postId IN (:postIds) "
    )
    fun getAuthorNamesAndPubkeysMapFlow(postIds: List<String>): Flow<Map<String, NameAndPubkey>>
}
