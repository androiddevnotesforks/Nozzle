package com.kaiwolfram.nozzle

import android.content.Context
import androidx.room.Room
import com.kaiwolfram.nozzle.data.nostr.INostrRepository
import com.kaiwolfram.nozzle.data.nostr.NostrRepositoryMock
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.postCardInteractor.PostCardInteractor
import com.kaiwolfram.nozzle.data.preferences.IPersonalProfileStorage
import com.kaiwolfram.nozzle.data.preferences.ProfilePreferences
import com.kaiwolfram.nozzle.data.room.AppDatabase

class AppContainer(context: Context) {
    val nostrRepository: INostrRepository by lazy {
        NostrRepositoryMock()
    }
    val profilePreferences: IPersonalProfileStorage by lazy {
        ProfilePreferences(context = context)
    }
    val roomDb: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "nozzle_database"
        ).build()
    }
    val postCardInteractor: IPostCardInteractor by lazy {
        PostCardInteractor(nostrRepository = nostrRepository, reactionDao = roomDb.reactionDao())
    }
}
