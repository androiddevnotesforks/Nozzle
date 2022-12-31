package com.kaiwolfram.nozzle.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.room.dao.ReactionDao
import com.kaiwolfram.nozzle.data.room.dao.RepostDao
import com.kaiwolfram.nozzle.data.room.entity.*

@Database(
    entities = [
        ContactEntity::class,
        PostEntity::class,
        ProfileEntity::class,
        ReactionEntity::class,
        RepostEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun profileDao(): ProfileDao
    abstract fun reactionDao(): ReactionDao
    abstract fun repostDao(): RepostDao
}
