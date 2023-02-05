package com.kaiwolfram.nozzle.ui.app.views.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.provider.IPersonalProfileProvider
import com.kaiwolfram.nozzle.data.provider.IRelayProvider
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.utils.*
import com.kaiwolfram.nozzle.model.FeedScreenContent
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
    val isContactsOnly: Boolean = true,
    val isPosts: Boolean = true,
    val isReplies: Boolean = true,
    val relays: List<String> = listOf(),
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
            renewSubscription(subBatchSize = SUB_BATCH_SIZE)
            setUIRelays(relayProvider.listRelays());
            setScreenContent(
                currentContent = viewModelState.value.screenContent,
                dbBatchSize = DB_BATCH_SIZE
            )
            setUIRefresh(false)
        }
    }

    val onRefreshFeedView: () -> Unit = {
        viewModelScope.launch(context = IO) {
            Log.i(TAG, "Refresh feed view")
            setUIRefresh(true)
            renewSubscription(subBatchSize = SUB_BATCH_SIZE)
            setUIRelays(relayProvider.listRelays());
            setScreenContent(
                currentContent = viewModelState.value.screenContent,
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
                subBatchSize = SUB_BATCH_SIZE,
                dbBatchSize = DB_BATCH_SIZE,
            )
        }
    }

    val onToggleContactsOnly: () -> Unit = {
        viewModelState.value.isContactsOnly.let { oldValue ->
            viewModelState.update {
                it.copy(isContactsOnly = !oldValue)
            }
        }
    }

    val onTogglePosts: () -> Unit = {
        viewModelState.value.let { oldValues ->
            if (oldValues.isReplies) {
                viewModelState.update {
                    it.copy(isPosts = !oldValues.isPosts)
                }
            }
        }
    }

    val onToggleReplies: () -> Unit = {
        viewModelState.value.let { oldValues ->
            if (oldValues.isPosts) {
                viewModelState.update {
                    it.copy(isReplies = !oldValues.isReplies)
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
        uiState.value.let { state ->
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

    private suspend fun renewSubscription(subBatchSize: Int) {
        subscribeToFeed(subBatchSize = subBatchSize)
        delay(1000)
        subscribeToAdditionalFeedData(feedProvider.getFeed(limit = subBatchSize))
        delay(1000)
    }

    private suspend fun subscribeToFeed(subBatchSize: Int, until: Long? = null) {
        Log.i(TAG, "Subscribe to feed")
        nostrSubscriber.unsubscribeFeeds()
        val pubkeys = listContactPubkeysAndYourself()
        Log.i(TAG, "Subscribe to feed of ${pubkeys.size} pubkeys")
        nostrSubscriber.subscribeToFeed(
            authorPubkeys = pubkeys,
            limit = subBatchSize,
            until = until
        )
    }

    private suspend fun subscribeToAdditionalFeedData(posts: List<PostWithMeta>) {
        Log.i(TAG, "Subscribe to additional feed data")
        nostrSubscriber.unsubscribeAdditionalPostsData()
        nostrSubscriber.subscribeToAdditionalPostsData(posts = posts)
    }

    private suspend fun setScreenContent(
        currentContent: FeedScreenContent,
        dbBatchSize: Int,
    ) {
        Log.i(TAG, "Set feed")
        val newFeed = feedProvider.getFeed(limit = dbBatchSize)
        val newScreenContent = currentContent.createWithNewFeed(newFeed = newFeed)
        setUIScreenContent(screenContent = newScreenContent)
    }

    private val isAppending = AtomicBoolean(false)

    private suspend fun fetchAndAppendFeed(
        currentScreenContent: FeedScreenContent,
        subBatchSize: Int,
        dbBatchSize: Int,
    ) {
        if (isAppending.get()) return

        Log.i(TAG, "Append feed")
        currentScreenContent.feed.lastOrNull()?.let { last ->
            isAppending.set(true)
            subscribeToFeed(subBatchSize = subBatchSize)
            delay(1000)
            val newPosts = feedProvider.getFeed(
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
        viewModelState.update { it.copy(relays = relayUrls) }
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
