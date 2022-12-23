package com.kaiwolfram.nozzle

import android.content.Context
import androidx.room.Room
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.nostr.NostrServiceMock
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.postCardInteractor.PostCardInteractor
import com.kaiwolfram.nozzle.data.preferences.key.IKeyManager
import com.kaiwolfram.nozzle.data.preferences.key.KeyPreferences
import com.kaiwolfram.nozzle.data.preferences.profile.IProfileCache
import com.kaiwolfram.nozzle.data.preferences.profile.ProfileCache
import com.kaiwolfram.nozzle.data.profileFollower.IProfileFollower
import com.kaiwolfram.nozzle.data.profileFollower.ProfileFollower
import com.kaiwolfram.nozzle.data.room.AppDatabase

class AppContainer(context: Context) {
    val nostrService: INostrService by lazy {
        NostrServiceMock()
    }
    val keyPreferences: IKeyManager by lazy {
        KeyPreferences(context = context)
    }
    val profileCache: IProfileCache by lazy {
        ProfileCache(pubkeyProvider = keyPreferences, context = context)
    }
    val roomDb: AppDatabase by lazy {
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "nozzle_database"
        ).build()
    }
    val postCardInteractor: IPostCardInteractor by lazy {
        PostCardInteractor(
            nostrService = nostrService,
            reactionDao = roomDb.reactionDao(),
            repostDao = roomDb.repostDao()
        )
    }
    val profileFollower: IProfileFollower by lazy {
        ProfileFollower(
            nostrService = nostrService,
            contactDao = roomDb.contactDao(),
        )
    }
}
