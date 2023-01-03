package com.kaiwolfram.nozzle.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kaiwolfram.nozzle.data.room.dao.*
import com.kaiwolfram.nozzle.data.room.entity.*

@Database(
    entities = [
        ContactEntity::class,
        PostEntity::class,
        ProfileEntity::class,
        ReactionEntity::class,
        ReplyEntity::class,
        RepostEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun profileDao(): ProfileDao
    abstract fun postDao(): PostDao
    abstract fun reactionDao(): ReactionDao
    abstract fun replyDao(): ReplyDao
    abstract fun repostDao(): RepostDao
}
