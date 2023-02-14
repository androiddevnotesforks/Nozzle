package com.kaiwolfram.nozzle.ui.app.views.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nostrclientkt.model.MultipleRelays
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.provider.IPersonalProfileProvider
import com.kaiwolfram.nozzle.data.provider.IRelayProvider
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.utils.*
import com.kaiwolfram.nozzle.model.FeedScreenContent
import com.kaiwolfram.nozzle.model.FeedSettings
import com.kaiwolfram.nozzle.model.HomeScreen
import com.kaiwolfram.nozzle.model.PostWithMeta
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "FeedViewModel"
private const val DB_BATCH_SIZE = 25
private const val SUB_BATCH_SIZE = 50

data class FeedViewModelState(
    /**
     * Current posts sorted from newest to oldest
     */
    val screenContent: FeedScreenContent = HomeScreen(listOf()),
    val isRefreshing: Boolean = false,
    val pubkey: String = "",
    val feedSettings: FeedSettings = FeedSettings(
        isContactsOnly = true,
        isPosts = true,
        isReplies = true,
        relays = listOf(),
    ),
)

class FeedViewModel(
    private val personalProfileProvider: IPersonalProfileProvider,
    private val feedProvider: IFeedProvider,
    private val relayProvider: IRelayProvider,
    private val postCardInteractor: IPostCardInteractor,
    private val nostrSubscriber: INostrSubscriber,
    private val contactDao: ContactDao,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(FeedViewModelState())

    var metadataState = personalProfileProvider.getMetadata().stateIn(
        viewModelScope, SharingStarted.Lazily, null
    )

    val uiState = viewModelState.stateIn(
        viewModelScope, SharingStarted.Eagerly, viewModelState.value
    )

    init {
        Log.i(TAG, "Initialize FeedViewModel")
        viewModelState.update {
            it.copy(pubkey = personalProfileProvider.getPubkey())
        }
        viewModelScope.launch(context = IO) {
            setUIRefresh(true)
            renewSubscriptionAndUI(
                feedSettings = viewModelState.value.feedSettings,
                currentContent = viewModelState.value.screenContent,
                updatedRelays = relayProvider.listRelays(),
                subBatchSize = SUB_BATCH_SIZE,
                dbBatchSize = DB_BATCH_SIZE
            )
            setUIRefresh(false)
        }
    }

    val onRefreshFeedView: () -> Unit = {
        viewModelScope.launch(context = IO) {
            Log.i(TAG, "Refresh feed view")
            setUIRefresh(true)
            renewSubscriptionAndUI(
                feedSettings = viewModelState.value.feedSettings,
                currentContent = viewModelState.value.screenContent,
                updatedRelays = relayProvider.listRelays(),
                subBatchSize = SUB_BATCH_SIZE,
                dbBatchSize = DB_BATCH_SIZE
            )
            setUIRefresh(false)
        }
    }

    val onLoadMore: () -> Unit = {
        viewModelScope.launch(context = IO) {
            Log.i(TAG, "Load more")
            fetchAndAppendFeed(
                currentScreenContent = viewModelState.value.screenContent,
                feedSettings = viewModelState.value.feedSettings,
                subBatchSize = SUB_BATCH_SIZE,
                dbBatchSize = DB_BATCH_SIZE,
            )
        }
    }

    private var toggledContacts = false
    private var toggledPosts = false
    private var toggledReplies = false

    val onRefreshOnMenuDismiss: () -> Unit = {
        if (toggledContacts || toggledPosts || toggledReplies) {
            onRefreshFeedView()
        }
        toggledContacts = false
        toggledPosts = false
        toggledReplies = false
    }

    val onToggleContactsOnly: () -> Unit = {
        viewModelState.value.feedSettings.isContactsOnly.let { oldValue ->
            viewModelState.update {
                this.toggledContacts = !this.toggledContacts
                it.copy(feedSettings = it.feedSettings.copy(isContactsOnly = !oldValue))
            }
        }
    }

    val onTogglePosts: () -> Unit = {
        viewModelState.value.feedSettings.let { oldSettings ->
            // Only changeable when isReplies is active
            if (oldSettings.isReplies) {
                viewModelState.update {
                    this.toggledPosts = !this.toggledPosts
                    it.copy(feedSettings = it.feedSettings.copy(isPosts = !oldSettings.isPosts))
                }
            }
        }
    }

    val onToggleReplies: () -> Unit = {
        viewModelState.value.feedSettings.let { oldSettings ->
            // Only changeable when isPosts is active
            if (oldSettings.isPosts) {
                viewModelState.update {
                    this.toggledReplies = !this.toggledReplies
                    it.copy(feedSettings = it.feedSettings.copy(isReplies = !oldSettings.isReplies))
                }
            }
        }
    }

    val onPreviousHeadline: () -> Unit = {
    }

    val onNextHeadline: () -> Unit = {
    }

    val onResetProfileIconUiState: () -> Unit = {
        Log.i(TAG, "Reset profile icon")
        metadataState = personalProfileProvider.getMetadata().stateIn(
            viewModelScope, SharingStarted.Lazily, null
        )
        viewModelState.update {
            it.copy(pubkey = personalProfileProvider.getPubkey())
        }
    }

    val onLike: (String) -> Unit = { id ->
        uiState.value.let { _ ->
            viewModelState.value.screenContent.feed.find { it.id == id }?.let {
                viewModelScope.launch(context = IO) {
                    postCardInteractor.like(postId = id, postPubkey = it.pubkey)
                }
            }
        }
    }

    val onRepost: (String) -> Unit = { id ->
        viewModelScope.launch(context = IO) {
            postCardInteractor.repost(postId = id)
        }
    }

    private suspend fun renewSubscriptionAndUI(
        feedSettings: FeedSettings,
        currentContent: FeedScreenContent,
        updatedRelays: List<String>,
        subBatchSize: Int,
        dbBatchSize: Int
    ) {
        // TODO: Renewing subsriptions should happen in feedprovider
        renewSubscription(feedSettings = feedSettings, subBatchSize = subBatchSize)
        setUIRelays(updatedRelays)
        getAndSetNewScreenContent(
            feedSettings = feedSettings,
            currentContent = currentContent,
            dbBatchSize = dbBatchSize
        )
    }

    private suspend fun renewSubscription(feedSettings: FeedSettings, subBatchSize: Int) {
        nostrSubscriber.unsubscribeProfiles()
        nostrSubscriber.subscribeToProfileMetadataAndContactList(
            pubkeys = listOf(personalProfileProvider.getPubkey())
        )
        subscribeToFeed(feedSettings = feedSettings, subBatchSize = subBatchSize)
        delay(1000)
        // TODO: Refactor using flows
        subscribeToAdditionalFeedData(feedProvider.getFeed(, limit = subBatchSize))
        delay(1000)
    }

    private suspend fun subscribeToFeed(
        feedSettings: FeedSettings,
        subBatchSize: Int,
        until: Long? = null
    ) {
        Log.i(TAG, "Subscribe to feed")
        nostrSubscriber.unsubscribeFeeds()
        nostrSubscriber.subscribeToFeed(
            authorPubkeys = if (feedSettings.isContactsOnly) listContactPubkeysAndYourself() else listOf(),
            limit = subBatchSize,
            until = until,
            relaySelection = MultipleRelays(relays = feedSettings.relays)
        )
    }

    private suspend fun subscribeToAdditionalFeedData(posts: List<PostWithMeta>) {
        Log.i(TAG, "Subscribe to additional feed data")
        nostrSubscriber.unsubscribeAdditionalPostsData()
        nostrSubscriber.subscribeToAdditionalPostsData(posts = posts)
    }

    private suspend fun getAndSetNewScreenContent(
        feedSettings: FeedSettings,
        currentContent: FeedScreenContent,
        dbBatchSize: Int,
    ) {
        Log.i(TAG, "Set feed")
        val newFeed = feedProvider.getFeed(, limit = dbBatchSize)
        val newScreenContent = currentContent.createWithNewFeed(newFeed = newFeed)
        setUIScreenContent(screenContent = newScreenContent)
    }

    private val isAppending = AtomicBoolean(false)

    private suspend fun fetchAndAppendFeed(
        currentScreenContent: FeedScreenContent,
        feedSettings: FeedSettings,
        subBatchSize: Int,
        dbBatchSize: Int,
    ) {
        if (isAppending.get()) return

        Log.i(TAG, "Append feed")
        currentScreenContent.feed.lastOrNull()?.let { last ->
            isAppending.set(true)
            subscribeToFeed(feedSettings = feedSettings, subBatchSize = subBatchSize)
            delay(1000)
            val newPosts = feedProvider.getFeed(
                ,
                limit = dbBatchSize,
                until = last.createdAt
            )
            subscribeToAdditionalFeedData(newPosts)
            val newScreenContent = currentScreenContent.createWithNewFeed(
                currentScreenContent.feed + newPosts
            )
            setUIScreenContent(screenContent = newScreenContent)
            isAppending.set(false)
        }
    }

    private fun setUIRefresh(value: Boolean) {
        viewModelState.update { it.copy(isRefreshing = value) }
    }

    private fun setUIScreenContent(screenContent: FeedScreenContent) {
        viewModelState.update { it.copy(screenContent = screenContent) }
    }

    private fun setUIRelays(relayUrls: List<String>) {
        viewModelState.update { it.copy(feedSettings = it.feedSettings.copy(relays = relayUrls)) }
    }

    private suspend fun listContactPubkeysAndYourself(): List<String> {
        val pubkeys = contactDao.listContactPubkeys(
            pubkey = personalProfileProvider.getPubkey()
        ).toMutableList()
        Log.i(TAG, "Found ${pubkeys.size} contact pubkeys")
        pubkeys.add(personalProfileProvider.getPubkey())

        return pubkeys
    }

    companion object {
        fun provideFactory(
            personalProfileProvider: IPersonalProfileProvider,
            feedProvider: IFeedProvider,
            relayProvider: IRelayProvider,
            postCardInteractor: IPostCardInteractor,
            nostrSubscriber: INostrSubscriber,
            contactDao: ContactDao,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(
                    personalProfileProvider = personalProfileProvider,
                    feedProvider = feedProvider,
                    relayProvider = relayProvider,
                    postCardInteractor = postCardInteractor,
                    nostrSubscriber = nostrSubscriber,
                    contactDao = contactDao,
                ) as T
            }
        }
    }
}
