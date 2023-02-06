package com.kaiwolfram.nozzle.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kaiwolfram.nozzle.data.room.dao.*
import com.kaiwolfram.nozzle.data.room.entity.*

@Database(
    entities = [
        ContactEntity::class,
        EventRelayEntity::class,
        PostEntity::class,
        ProfileEntity::class,
        ReactionEntity::class,
        RelayEntity::class,
        UserRelayEntity::class,
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun eventRelayDao(): EventRelayDao
    abstract fun profileDao(): ProfileDao
    abstract fun postDao(): PostDao
    abstract fun reactionDao(): ReactionDao
    abstract fun relayDao(): RelayDao
    abstract fun userRelayDao(): UserRelayDao
}
