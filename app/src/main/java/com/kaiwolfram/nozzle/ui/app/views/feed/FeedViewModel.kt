package com.kaiwolfram.nozzle.ui.app.views.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nostrclientkt.model.AllRelays
import com.kaiwolfram.nostrclientkt.model.MultipleRelays
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.provider.IPersonalProfileProvider
import com.kaiwolfram.nozzle.data.provider.IRelayProvider
import com.kaiwolfram.nozzle.data.utils.*
import com.kaiwolfram.nozzle.model.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "FeedViewModel"
private const val DB_BATCH_SIZE = 30

data class FeedViewModelState(
    val isRefreshing: Boolean = false,
    val pubkey: String = "",
    val feedSettings: FeedSettings = FeedSettings(
        isPosts = true,
        isReplies = true,
        authorSelection = Contacts,
        relaySelection = AllRelays,
    ),
)

class FeedViewModel(
    private val personalProfileProvider: IPersonalProfileProvider,
    private val feedProvider: IFeedProvider,
    private val relayProvider: IRelayProvider,
    private val postCardInteractor: IPostCardInteractor,
    private val nostrSubscriber: INostrSubscriber,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(FeedViewModelState())
    val uiState = viewModelState.stateIn(
        viewModelScope, SharingStarted.Eagerly, viewModelState.value
    )

    var metadataState = personalProfileProvider.getMetadata().stateIn(
        viewModelScope, SharingStarted.Lazily, null
    )

    var feedState: StateFlow<List<PostWithMeta>> = MutableStateFlow(listOf())

    // TODO: Figure out how to do it without this hack
    private val forceRecomposition = MutableStateFlow(0)
    val forceRecompositionState = forceRecomposition
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            0
        )

    init {
        Log.i(TAG, "Initialize FeedViewModel")
        viewModelState.update {
            it.copy(pubkey = personalProfileProvider.getPubkey())
        }
        viewModelScope.launch(context = IO) {
            setUIRefresh(true)
            subscribeToPersonalProfile()
            updateScreen(
                feedSettings = viewModelState.value.feedSettings,
                updatedRelays = relayProvider.listRelays(),
                dbBatchSize = DB_BATCH_SIZE
            )
            setUIRefresh(false)
        }
    }

    val onRefreshFeedView: () -> Unit = {
        viewModelScope.launch(context = IO) {
            Log.i(TAG, "Refresh feed view")
            setUIRefresh(true)
            subscribeToPersonalProfile()
            updateScreen(
                feedSettings = viewModelState.value.feedSettings,
                updatedRelays = relayProvider.listRelays(),
                dbBatchSize = DB_BATCH_SIZE
            )
            setUIRefresh(false)
        }
    }

    val onLoadMore: () -> Unit = {
        viewModelScope.launch(context = IO) {
            Log.i(TAG, "Load more")
            appendFeed(
                currentFeed = feedState.value,
                feedSettings = viewModelState.value.feedSettings,
                dbBatchSize = DB_BATCH_SIZE,
            )
            forceRecomposition.update { it + 1 }
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
        viewModelState.value.feedSettings.authorSelection.let { oldValue ->
            viewModelState.update {
                this.toggledContacts = !this.toggledContacts
                val newValue = when (oldValue) {
                    is Everyone -> Contacts
                    is Contacts -> Everyone
                    is SingleAuthor -> {
                        Log.w(
                            TAG,
                            "ContactsOnly is set to SingleAuthor, which shouldn't be possible"
                        )
                        Contacts
                    }
                }
                it.copy(feedSettings = it.feedSettings.copy(authorSelection = newValue))
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
            feedState.value.find { it.id == id }?.let {
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

    private suspend fun updateScreen(
        feedSettings: FeedSettings,
        updatedRelays: List<String>,
        dbBatchSize: Int
    ) {
        setUIRelays(updatedRelays)
        feedState = feedProvider.getFeed(
            feedSettings = feedSettings,
            limit = dbBatchSize,
            waitForSubscription = false
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            feedState.value,
        )
        delay(2000)
    }

    private val isAppending = AtomicBoolean(false)

    private fun appendFeed(
        currentFeed: List<PostWithMeta>,
        feedSettings: FeedSettings,
        dbBatchSize: Int,
    ) {
        if (isAppending.get()) return

        currentFeed.lastOrNull()?.let { last ->
            Log.i(TAG, "Append feed")
            isAppending.set(true)
            feedState = feedProvider.getFeed(
                feedSettings = feedSettings,
                limit = dbBatchSize,
                until = last.createdAt
            ).map { toAppend -> currentFeed + toAppend }
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(),
                    currentFeed,
                )
            Log.i(TAG, "New feed length ${feedState.value.size}")
            isAppending.set(false)
        }
    }

    private fun subscribeToPersonalProfile() {
        nostrSubscriber.unsubscribeProfiles()
        nostrSubscriber.subscribeToProfileMetadataAndContactList(
            pubkeys = listOf(
                personalProfileProvider.getPubkey()
            )
        )
    }

    private fun setUIRefresh(value: Boolean) {
        viewModelState.update { it.copy(isRefreshing = value) }
    }

    private fun setUIRelays(relayUrls: List<String>) {
        viewModelState.update {
            it.copy(
                feedSettings = it.feedSettings.copy(
                    relaySelection = MultipleRelays(
                        relays = relayUrls
                    )
                )
            )
        }
    }

    companion object {
        fun provideFactory(
            personalProfileProvider: IPersonalProfileProvider,
            feedProvider: IFeedProvider,
            relayProvider: IRelayProvider,
            postCardInteractor: IPostCardInteractor,
            nostrSubscriber: INostrSubscriber,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(
                    personalProfileProvider = personalProfileProvider,
                    feedProvider = feedProvider,
                    relayProvider = relayProvider,
                    postCardInteractor = postCardInteractor,
                    nostrSubscriber = nostrSubscriber,
                ) as T
            }
        }
    }
}
