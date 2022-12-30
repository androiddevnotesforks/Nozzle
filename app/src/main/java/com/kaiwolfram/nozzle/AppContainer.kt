package com.kaiwolfram.nozzle

import android.content.Context
import androidx.room.Room
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.nostr.NostrService
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.postCardInteractor.PostCardInteractor
import com.kaiwolfram.nozzle.data.preferences.key.IKeyManager
import com.kaiwolfram.nozzle.data.preferences.key.KeyPreferences
import com.kaiwolfram.nozzle.data.preferences.profile.IProfileCache
import com.kaiwolfram.nozzle.data.preferences.profile.ProfileCache
import com.kaiwolfram.nozzle.data.profileFollower.IProfileFollower
import com.kaiwolfram.nozzle.data.profileFollower.ProfileFollower
import com.kaiwolfram.nozzle.data.provider.FeedProvider
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.provider.IThreadProvider
import com.kaiwolfram.nozzle.data.provider.ThreadProvider
import com.kaiwolfram.nozzle.data.room.AppDatabase

class AppContainer(context: Context) {

    val keyPreferences: IKeyManager = KeyPreferences(context = context)

    val nostrService: INostrService = NostrService(keyManager = keyPreferences)

    val profileCache: IProfileCache = ProfileCache(
        pubkeyProvider = keyPreferences,
        context = context
    )

    val roomDb: AppDatabase by lazy {
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "nozzle_database"
        ).build()
    }
    val postCardInteractor: IPostCardInteractor = PostCardInteractor(
        nostrService = nostrService,
        pubkeyProvider = keyPreferences,
        reactionDao = roomDb.reactionDao(),
        repostDao = roomDb.repostDao()
    )

    val profileFollower: IProfileFollower = ProfileFollower(
        nostrService = nostrService,
        pubkeyProvider = keyPreferences,
        contactDao = roomDb.contactDao()
    )

    val feedProvider: IFeedProvider = FeedProvider()

    val threadProvider: IThreadProvider = ThreadProvider()
}
