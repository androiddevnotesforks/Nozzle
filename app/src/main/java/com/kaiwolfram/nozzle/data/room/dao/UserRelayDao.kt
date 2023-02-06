package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Upsert
import com.kaiwolfram.nozzle.data.room.entity.UserRelayEntity

@Dao
interface UserRelayDao {
    @Upsert(entity = UserRelayEntity::class)
    suspend fun upsert(userRelay: UserRelayEntity)

    @Insert(entity = UserRelayEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(userRelay: UserRelayEntity)
}
