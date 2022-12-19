package com.kaiwolfram.nozzle

import android.content.Context
import androidx.room.Room
import com.kaiwolfram.nozzle.data.nostr.INostrRepository
import com.kaiwolfram.nozzle.data.nostr.NostrRepositoryMock
import com.kaiwolfram.nozzle.data.preferences.ProfilePreferences
import com.kaiwolfram.nozzle.data.room.AppDatabase

class AppContainer(context: Context) {
    val nostrRepository: INostrRepository by lazy {
        NostrRepositoryMock()
    }
    val profilePreferences: ProfilePreferences by lazy {
        ProfilePreferences(context = context)
    }
    val roomDb: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "nozzle_database"
        ).build()
    }
}
