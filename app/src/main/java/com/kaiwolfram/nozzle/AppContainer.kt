package com.kaiwolfram.nozzle

import android.content.Context
import androidx.room.Room
import com.kaiwolfram.nozzle.data.eventProcessor.EventProcessor
import com.kaiwolfram.nozzle.data.eventProcessor.IEventProcessor
import com.kaiwolfram.nozzle.data.manager.IKeyManager
import com.kaiwolfram.nozzle.data.manager.IPersonalProfileManager
import com.kaiwolfram.nozzle.data.manager.impl.KeyManager
import com.kaiwolfram.nozzle.data.manager.impl.PersonalProfileManager
import com.kaiwolfram.nozzle.data.mapper.IPostMapper
import com.kaiwolfram.nozzle.data.mapper.PostMapper
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.nostr.NostrService
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.postCardInteractor.PostCardInteractor
import com.kaiwolfram.nozzle.data.profileFollower.IProfileFollower
import com.kaiwolfram.nozzle.data.profileFollower.ProfileFollower
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.provider.IInteractionStatsProvider
import com.kaiwolfram.nozzle.data.provider.IProfileWithFollowerProvider
import com.kaiwolfram.nozzle.data.provider.IThreadProvider
import com.kaiwolfram.nozzle.data.provider.impl.FeedProvider
import com.kaiwolfram.nozzle.data.provider.impl.InteractionStatsProvider
import com.kaiwolfram.nozzle.data.provider.impl.ProfileWithFollowerProvider
import com.kaiwolfram.nozzle.data.provider.impl.ThreadProvider
import com.kaiwolfram.nozzle.data.room.AppDatabase

class AppContainer(context: Context) {

    val roomDb: AppDatabase by lazy {
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "nozzle_database"
        ).build()
    }

    val keyManager: IKeyManager = KeyManager(context = context)

    val eventProcessor: IEventProcessor = EventProcessor(
        reactionDao = roomDb.reactionDao(),
        profileDao = roomDb.profileDao(),
        contactDao = roomDb.contactDao(),
        postDao = roomDb.postDao(),
    )

    val nostrService: INostrService = NostrService(
        keyManager = keyManager,
        eventProcessor = eventProcessor
    )

    val postCardInteractor: IPostCardInteractor = PostCardInteractor(
        nostrService = nostrService,
        reactionDao = roomDb.reactionDao(),
        postDao = roomDb.postDao()
    )

    val profileFollower: IProfileFollower = ProfileFollower(
        nostrService = nostrService,
        pubkeyProvider = keyManager,
        contactDao = roomDb.contactDao()
    )

    val interactionStatsProvider: IInteractionStatsProvider = InteractionStatsProvider(
        pubkeyProvider = keyManager,
        reactionDao = roomDb.reactionDao(),
        postDao = roomDb.postDao()
    )

    val postMapper: IPostMapper = PostMapper(
        interactionStatsProvider = interactionStatsProvider,
        postDao = roomDb.postDao(),
        profileDao = roomDb.profileDao()
    )

    val feedProvider: IFeedProvider = FeedProvider(
        pubkeyProvider = keyManager,
        postMapper = postMapper,
        postDao = roomDb.postDao(),
        contactDao = roomDb.contactDao(),
    )

    val profileWithFollowerProvider: IProfileWithFollowerProvider = ProfileWithFollowerProvider(
        pubkeyProvider = keyManager,
        profileDao = roomDb.profileDao(),
        contactDao = roomDb.contactDao()
    )

    val personalProfileManager: IPersonalProfileManager = PersonalProfileManager(
        pubkeyProvider = keyManager,
        profileDao = roomDb.profileDao()
    )

    val threadProvider: IThreadProvider = ThreadProvider(
        postMapper = postMapper,
        postDao = roomDb.postDao()
    )
}
