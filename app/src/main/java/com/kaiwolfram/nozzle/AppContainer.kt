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
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import com.kaiwolfram.nozzle.data.nostr.NostrService
import com.kaiwolfram.nozzle.data.nostr.NostrSubscriber
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.postCardInteractor.PostCardInteractor
import com.kaiwolfram.nozzle.data.profileFollower.IProfileFollower
import com.kaiwolfram.nozzle.data.profileFollower.ProfileFollower
import com.kaiwolfram.nozzle.data.provider.*
import com.kaiwolfram.nozzle.data.provider.impl.*
import com.kaiwolfram.nozzle.data.room.AppDatabase

class AppContainer(context: Context) {

    val roomDb: AppDatabase by lazy {
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "nozzle_database"
        ).build()
    }

    val relayProvider: IRelayProvider = RelayProvider(
        relayDao = roomDb.relayDao()
    )

    val keyManager: IKeyManager = KeyManager(context = context)

    private val eventProcessor: IEventProcessor = EventProcessor(
        reactionDao = roomDb.reactionDao(),
        profileDao = roomDb.profileDao(),
        contactDao = roomDb.contactDao(),
        postDao = roomDb.postDao(),
        eventRelayDao = roomDb.eventRelayDao(),
    )

    val nostrService: INostrService = NostrService(
        keyManager = keyManager,
        relayProvider = relayProvider,
        eventProcessor = eventProcessor
    )

    val nostrSubscriber: INostrSubscriber = NostrSubscriber(
        nostrService = nostrService,
        postDao = roomDb.postDao()
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

    private val interactionStatsProvider: IInteractionStatsProvider = InteractionStatsProvider(
        pubkeyProvider = keyManager,
        reactionDao = roomDb.reactionDao(),
        postDao = roomDb.postDao()
    )

    private val postMapper: IPostMapper = PostMapper(
        interactionStatsProvider = interactionStatsProvider,
        postDao = roomDb.postDao(),
        profileDao = roomDb.profileDao(),
        eventRelayDao = roomDb.eventRelayDao(),
    )

    val feedProvider: IFeedProvider = FeedProvider(
        pubkeyProvider = keyManager,
        postMapper = postMapper,
        nostrSubscriber = nostrSubscriber,
        postDao = roomDb.postDao(),
    )

    val profileWithFollowerProvider: IProfileWithAdditionalInfoProvider =
        ProfileWithAdditionalInfoProvider(
            pubkeyProvider = keyManager,
            nostrSubscriber = nostrSubscriber,
            profileDao = roomDb.profileDao(),
            contactDao = roomDb.contactDao(),
            eventRelayDao = roomDb.eventRelayDao(),
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
