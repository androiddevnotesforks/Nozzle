package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.kaiwolfram.nozzle.data.room.entity.PostEntity

@Dao
interface FeedDao {
    @Query(
        "SELECT * " +
                "FROM event " +
                "WHERE pubkey " +
                "IN (SELECT contactPubkey FROM contact WHERE pubkey = :pubkey)"
    )
    fun getFeed(pubkey: String): List<PostEntity>
}
