package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.*
import com.kaiwolfram.nostrclientkt.Metadata
import com.kaiwolfram.nozzle.data.room.entity.ProfileEntity
import com.kaiwolfram.nozzle.model.NameAndPicture

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE pubkey = :pubkey")
    suspend fun getProfile(pubkey: String): ProfileEntity?

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM profile WHERE pubkey = :pubkey")
    suspend fun getMetadata(pubkey: String): Metadata?

    @Query(
        "UPDATE profile " +
                "SET name = :name, " +
                "about = :about, " +
                "picture = :picture, " +
                "nip05 = :nip05 " +
                "WHERE pubkey = :pubkey"
    )
    suspend fun updateMetadata(
        pubkey: String,
        name: String,
        about: String,
        picture: String,
        nip05: String,
    )

    // TODO: Replace if createdAt is larger
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceIfNewer(vararg profile: ProfileEntity)

    @Query(
        "DELETE FROM profile " +
                "WHERE pubkey = :pubkey AND createdAt < :createdAt"
    )
    suspend fun deleteIfOutdated(pubkey: String, createdAt: Long)

    @MapInfo(keyColumn = "pubkey")
    @Query(
        "SELECT * " +
                "FROM profile " +
                "WHERE pubkey IN (:pubkeys) "
    )
    suspend fun getNamesAndPicturesMap(pubkeys: List<String>): Map<String, NameAndPicture>
}
