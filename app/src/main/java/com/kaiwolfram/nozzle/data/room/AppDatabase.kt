package com.kaiwolfram.nozzle.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kaiwolfram.nozzle.data.room.dao.EventDao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.room.entity.EventEntity
import com.kaiwolfram.nozzle.data.room.entity.ProfileEntity

@Database(entities = [EventEntity::class, ProfileEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun eventDao(): EventDao
}
