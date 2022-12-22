package com.kaiwolfram.nozzle.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.kaiwolfram.nozzle.data.room.entity.ContactEntity

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg contactEntity: ContactEntity)

    @Delete
    suspend fun delete(vararg contactEntity: ContactEntity)
}
