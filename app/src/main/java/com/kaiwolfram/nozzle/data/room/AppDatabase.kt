package com.kaiwolfram.nozzle.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kaiwolfram.nozzle.data.room.dao.EventDao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.room.dao.ReactionDao
import com.kaiwolfram.nozzle.data.room.dao.RepostDao
import com.kaiwolfram.nozzle.data.room.entity.EventEntity
import com.kaiwolfram.nozzle.data.room.entity.ProfileEntity
import com.kaiwolfram.nozzle.data.room.entity.ReactionEntity
import com.kaiwolfram.nozzle.data.room.entity.RepostEntity

@Database(
    entities = [EventEntity::class,
        ProfileEntity::class,
        ReactionEntity::class,
        RepostEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun eventDao(): EventDao
    abstract fun reactionDao(): ReactionDao
    abstract fun repostDao(): RepostDao
}
