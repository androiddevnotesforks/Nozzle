package com.kaiwolfram.nozzle

import android.content.Context
import androidx.room.Room
import com.kaiwolfram.nozzle.data.eventProcessor.EventProcessor
import com.kaiwolfram.nozzle.data.eventProcessor.IEventProcessor
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
import com.kaiwolfram.nozzle.data.provider.*
import com.kaiwolfram.nozzle.data.room.AppDatabase

class AppContainer(context: Context) {

    val roomDb: AppDatabase by lazy {
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "nozzle_database"
        ).build()
    }

    val keyPreferences: IKeyManager = KeyPreferences(context = context)

    val eventProcessor: IEventProcessor = EventProcessor(
        reactionDao = roomDb.reactionDao(),
        profileDao = roomDb.profileDao(),
        contactDao = roomDb.contactDao(),
        postDao = roomDb.postDao(),
    )

    val nostrService: INostrService = NostrService(
        keyManager = keyPreferences,
        eventProcessor = eventProcessor
    )

    val profileCache: IProfileCache = ProfileCache(
        pubkeyProvider = keyPreferences,
        context = context
    )

    val postCardInteractor: IPostCardInteractor = PostCardInteractor(
        nostrService = nostrService,
        reactionDao = roomDb.reactionDao(),
        repostDao = roomDb.repostDao()
    )

    val profileFollower: IProfileFollower = ProfileFollower(
        nostrService = nostrService,
        pubkeyProvider = keyPreferences,
        contactDao = roomDb.contactDao()
    )

    val feedProvider: IFeedProvider = FeedProvider(
        pubkeyProvider = keyPreferences,
        postDao = roomDb.postDao(),
        contactDao = roomDb.contactDao()
    )

    val profileProvider: IProfileProvider = ProfileProvider(
        pubkeyProvider = keyPreferences,
        profileDao = roomDb.profileDao(),
        contactDao = roomDb.contactDao()
    )

    val threadProvider: IThreadProvider = ThreadProvider()
}
