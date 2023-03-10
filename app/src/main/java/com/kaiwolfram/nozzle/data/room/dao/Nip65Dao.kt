package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.*
import com.kaiwolfram.nozzle.data.room.entity.Nip65Entity


@Dao
interface Nip65Dao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(vararg nip65Entries: Nip65Entity)

    @Query(
        "DELETE FROM nip65 " +
                "WHERE pubkey = :pubkey AND createdAt < :newTimestamp"
    )
    suspend fun deleteIfOutdated(pubkey: String, newTimestamp: Long)

    @Transaction
    suspend fun insertAndDeleteOutdated(
        pubkey: String,
        timestamp: Long,
        vararg nip65Entities: Nip65Entity
    ) {
        if (nip65Entities.isEmpty()) return

        deleteIfOutdated(pubkey = pubkey, newTimestamp = timestamp)
        insertOrIgnore(*nip65Entities)
    }
}
