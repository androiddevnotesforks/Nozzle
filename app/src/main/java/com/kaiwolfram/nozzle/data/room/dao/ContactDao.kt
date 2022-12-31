package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.*
import com.kaiwolfram.nozzle.data.room.entity.ContactEntity

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertContact(vararg contactEntity: ContactEntity)

    @Delete
    suspend fun deleteContact(vararg contactEntity: ContactEntity)

    @Query(
        "SELECT contactPubkey " +
                "FROM contact " +
                "WHERE pubkey = :pubkey"
    )
    suspend fun listContacts(pubkey: String): List<String>
}
