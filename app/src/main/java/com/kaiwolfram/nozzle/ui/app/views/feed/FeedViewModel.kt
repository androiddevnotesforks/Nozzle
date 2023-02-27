package com.kaiwolfram.nozzle.ui.app.views.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nostrclientkt.model.AllRelays
import com.kaiwolfram.nostrclientkt.model.MultipleRelays
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.preferences.IFeedSettingsPreferences
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
private const val WAIT_TIME = 2100L

data class FeedViewModelState(
    val isRefreshing: Boolean = false,
    val pubkey: String = "",
    val feedSettings: FeedSettings = FeedSettings(
        isPosts = true,
        isReplies = true,
        authorSelection = Contacts,
        relaySelection = AllRelays,
    ),
    val relayStatuses: List<RelayActive> = listOf(),
)

class FeedViewModel(
    private val personalProfileProvider: IPersonalProfileProvider,
    private val feedProvider: IFeedProvider,
    private val relayProvider: IRelayProvider,
    private val postCardInteractor: IPostCardInteractor,
    private val nostrSubscriber: INostrSubscriber,
    private val feedSettingsPreferences: IFeedSettingsPreferences,
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
            it.copy(
                pubkey = personalProfileProvider.getPubkey(),
                feedSettings = feedSettingsPreferences.getFeedSettings(),
                // TODO: remember selected relays
                relayStatuses = listRelayStatuses(
                    allRelayUrls = relayProvider.listRelays(),
                    relaySelection = AllRelays
                )
            )
        }
        viewModelScope.launch(context = IO) {
            handleRefresh()
        }
    }

    val onRefreshFeedView: () -> Unit = {
        viewModelScope.launch(context = IO) {
            Log.i(TAG, "Refresh feed view")
            handleRefresh()
        }
    }

    val onLoadMore: () -> Unit = {
        viewModelScope.launch(context = IO) {
            Log.i(TAG, "Load more")
            appendFeed(currentFeed = feedState.value)
            forceRecomposition.update { it + 1 }
        }
    }

    private var toggledContacts = false
    private var toggledPosts = false
    private var toggledReplies = false

    val onRefreshOnMenuDismiss: () -> Unit = {
        if (toggledContacts || toggledPosts || toggledReplies) {
            onRefreshFeedView()
            feedSettingsPreferences.setFeedSettings(viewModelState.value.feedSettings)
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

    // TODO: Refresh on dismiss
    val onToggleRelayIndex: (Int) -> Unit = { index ->
        val toggled = toggleRelay(relays = viewModelState.value.relayStatuses, index = index)
        if (toggled.any { it.isActive }) {
            updateRelaySelection(newRelayStatuses = toggled)
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

    private suspend fun handleRefresh() {
        setUIRefresh(true)
        subscribeToPersonalProfile()
        updateScreen()
        setUIRefresh(false)
        delay(WAIT_TIME)
        renewAdditionalDataSubscription(feedState.value)
    }

    private suspend fun updateScreen() {
        updateRelaySelection()
        feedState = feedProvider.getFeedFlow(
            feedSettings = viewModelState.value.feedSettings,
            limit = DB_BATCH_SIZE,
            waitForSubscription = WAIT_TIME,
        ).stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            feedState.value,
        )
    }

    private val isAppending = AtomicBoolean(false)

    private suspend fun appendFeed(currentFeed: List<PostWithMeta>) {
        if (isAppending.get()) return

        currentFeed.lastOrNull()?.let { last ->
            Log.i(TAG, "Append feed")
            isAppending.set(true)
            feedState = feedProvider.getFeedFlow(
                feedSettings = viewModelState.value.feedSettings,
                limit = DB_BATCH_SIZE,
                until = last.createdAt,
            ).map { toAppend -> currentFeed + toAppend }
                .stateIn(
                    viewModelScope,
                    SharingStarted.Eagerly,
                    currentFeed,
                )
            Log.i(TAG, "New feed length ${feedState.value.size}")
            isAppending.set(false)
        }
    }

    // TODO: Consider relaySelection
    private fun subscribeToPersonalProfile() {
        nostrSubscriber.unsubscribeProfiles()
        nostrSubscriber.subscribeToProfileMetadataAndContactList(
            pubkeys = listOf(
                personalProfileProvider.getPubkey()
            )
        )
    }

    // TODO: Consider relaySelection
    private suspend fun renewAdditionalDataSubscription(posts: List<PostWithMeta>) {
        nostrSubscriber.unsubscribeAdditionalPostsData()
        nostrSubscriber.subscribeToAdditionalPostsData(posts = posts)
    }

    private fun setUIRefresh(value: Boolean) {
        viewModelState.update { it.copy(isRefreshing = value) }
    }

    private fun updateRelaySelection(newRelayStatuses: List<RelayActive>? = null) {
        val selectedRelays = newRelayStatuses?.filter { it.isActive }?.map { it.relayUrl }
            ?: viewModelState.value.relayStatuses
                .filter { it.isActive }
                .map { it.relayUrl }
        val relaySelection = MultipleRelays(selectedRelays)
        val newStatuses = newRelayStatuses ?: listRelayStatuses(
            allRelayUrls = relayProvider.listRelays(),
            relaySelection = relaySelection
        )
        viewModelState.update {
            it.copy(
                relayStatuses = newStatuses,
                feedSettings = it.feedSettings.copy(relaySelection = relaySelection)
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
            feedSettingsPreferences: IFeedSettingsPreferences,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(
                    personalProfileProvider = personalProfileProvider,
                    feedProvider = feedProvider,
                    relayProvider = relayProvider,
                    postCardInteractor = postCardInteractor,
                    nostrSubscriber = nostrSubscriber,
                    feedSettingsPreferences = feedSettingsPreferences,
                ) as T
            }
        }
    }
}
