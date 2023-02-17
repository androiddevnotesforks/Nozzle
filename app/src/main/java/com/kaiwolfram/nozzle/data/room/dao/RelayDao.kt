package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RelayDao {
    @Query(
        "INSERT OR IGNORE INTO relay (relayUrl) " +
                "VALUES (:relayUrl)"
    )
    suspend fun insertOrIgnore(relayUrl: String)

    @Query("SELECT relayUrl FROM relay")
    fun listRelays(): Flow<List<String>>

    @Query("DELETE FROM relay")
    fun deleteAll()
}
