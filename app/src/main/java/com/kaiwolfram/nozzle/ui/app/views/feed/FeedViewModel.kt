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
import com.kaiwolfram.nozzle.model.PostWithMeta
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "FeedViewModel"
private const val BATCH_SIZE = 25

data class FeedViewModelState(
    /**
     * Current posts sorted from newest to oldest
     */
    val posts: List<PostWithMeta> = mutableListOf(),
    val isRefreshing: Boolean = false,
    val pubkey: String = "",
    val currentRelay: String = ""
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
    private val feedMap = mutableMapOf<String, List<PostWithMeta>>()

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
            setRefresh(true)
            renewAllSubscriptions()
            val relays = relayProvider.listRelays()
            Log.i(TAG, "Listed ${relays.size} relays")
            viewModelState.update {
                it.copy(currentRelay = relays.firstOrNull().orEmpty())
            }
            setAllFeeds(relayUrls = relays, feedMap = feedMap)
            setRefresh(false)
        }
    }

    val onRefreshFeedView: () -> Unit = {
        viewModelScope.launch(context = IO) {
            Log.i(TAG, "Refresh feed view")
            setRefresh(true)
            renewAllSubscriptions()
            setAllFeeds(relayUrls = relayProvider.listRelays(), feedMap = feedMap)
            setRefresh(false)
        }
    }

    val onLoadMore: () -> Unit = {
        viewModelScope.launch(context = IO) {
            Log.i(TAG, "Load more")
            fetchAndAppendFeedByRelay(
                relayUrl = uiState.value.currentRelay,
                feedMap = feedMap,
            )
        }
    }

    val onPreviousHeadline: () -> Unit = {
        val relayUrls = relayProvider.listRelays()
        val newRelayUrl = when (val currentIndex = relayUrls.indexOf(uiState.value.currentRelay)) {
            0 -> relayUrls.last()
            in 1 until relayUrls.size -> relayUrls[currentIndex - 1]
            else -> relayUrls.first()
        }
        Log.i(TAG, "Previous relay is $newRelayUrl")
        setRelayAndPosts(relayUrl = newRelayUrl, posts = feedMap[newRelayUrl].orEmpty())
    }

    val onNextHeadline: () -> Unit = {
        val relayUrls = relayProvider.listRelays()
        val newRelayUrl = when (val currentIndex = relayUrls.indexOf(uiState.value.currentRelay)) {
            in 0 until relayUrls.size - 1 -> relayUrls[currentIndex + 1]
            else -> relayUrls.first()
        }
        Log.i(TAG, "Next relay is $newRelayUrl")
        setRelayAndPosts(relayUrl = newRelayUrl, posts = feedMap[newRelayUrl].orEmpty())
    }

    val onResetProfileIconUiState: () -> Unit = {
        Log.i(TAG, "Reset profile icon")
        metadataState = personalProfileProvider.getMetadata().stateIn(
            viewModelScope, SharingStarted.Lazily, null
        )
        viewModelState.update {
            it.copy(
                pubkey = personalProfileProvider.getPubkey(),
            )
        }
    }

    val onLike: (String) -> Unit = { id ->
        uiState.value.let { state ->
            state.posts.find { it.id == id }?.let {
                viewModelScope.launch(context = IO) {
                    postCardInteractor.like(postId = id, postPubkey = it.pubkey)
                }
                viewModelState.update {
                    it.copy(
                        posts = state.posts.map { toMap ->
                            mapToLikedPost(toMap = toMap, id = id)
                        },
                    )
                }
            }
        }
    }

    val onRepost: (String) -> Unit = { id ->
        uiState.value.let { state ->
            if (state.posts.any { post -> post.id == id }) {
                viewModelScope.launch(context = IO) {
                    postCardInteractor.repost(postId = id)
                }
                viewModelState.update {
                    it.copy(
                        posts = state.posts.map { toMap ->
                            mapToRepostedPost(toMap = toMap, id = id)
                        },
                    )
                }
            }
        }
    }

    private suspend fun renewAllSubscriptions() {
        subscribeToAllFeeds()
        delay(1000)
        subscribeToAdditionalFeedData(feedProvider.getFeed(limit = 2 * BATCH_SIZE))
        delay(2000)
    }

    private suspend fun subscribeToAllFeeds(until: Long? = null) {
        Log.i(TAG, "Subscribe to all feeds")
        nostrSubscriber.unsubscribeFeeds()
        val pubkeys = listContactPubkeysAndYourself()
        Log.i(TAG, "Found ${pubkeys.size} contact pubkeys")
        nostrSubscriber.subscribeToFeed(
            authorPubkeys = pubkeys,
            limit = BATCH_SIZE,
            until = until
        )
    }

    private suspend fun subscribeToFeedByRelay(relayUrl: String, until: Long? = null) {
        Log.i(TAG, "Subscribe to feed of $relayUrl")
        nostrSubscriber.unsubscribeFeeds()
        val pubkeys = listContactPubkeysAndYourself()
        nostrSubscriber.subscribeToFeedByRelay(
            relayUrl = relayUrl,
            authorPubkeys = pubkeys,
            limit = BATCH_SIZE,
            until = until
        )
    }

    private suspend fun subscribeToAdditionalFeedData(posts: List<PostWithMeta>) {
        Log.i(TAG, "Subscribe to additional feed data")
        nostrSubscriber.unsubscribeAdditionalPostsData()
        nostrSubscriber.subscribeToAdditionalPostsData(posts = posts)
    }

    private suspend fun setAllFeeds(
        relayUrls: List<String>,
        feedMap: MutableMap<String, List<PostWithMeta>>
    ) {
        Log.i(TAG, "Set all feeds")
        relayUrls.forEach { relayUrl ->
            val feedByRelay = feedProvider.getFeedByRelay(relayUrl = relayUrl, limit = BATCH_SIZE)
            feedMap[relayUrl] = feedByRelay
            if (relayUrl == uiState.value.currentRelay) {
                viewModelState.update {
                    it.copy(posts = feedByRelay)
                }
            }
            Log.i(TAG, "Feed for relay $relayUrl contains ${feedByRelay.size} posts")
        }
    }

    private val isAppending = AtomicBoolean(false)

    private suspend fun fetchAndAppendFeedByRelay(
        relayUrl: String,
        feedMap: MutableMap<String, List<PostWithMeta>>,
    ) {
        if (isAppending.get()) return

        viewModelState.value.let { state ->
            Log.i(TAG, "Append feed for relay $relayUrl")
            state.posts.lastOrNull()?.let { last ->
                isAppending.set(true)
                subscribeToFeedByRelay(relayUrl = relayUrl, until = last.createdAt)
                delay(1000)
                val newPosts = appendFeedAndGetNewPosts(relayUrl = relayUrl, feedMap = feedMap)
                subscribeToAdditionalFeedData(newPosts)
                isAppending.set(false)
            }
        }
    }

    private suspend fun appendFeedAndGetNewPosts(
        relayUrl: String,
        feedMap: MutableMap<String, List<PostWithMeta>>
    ): List<PostWithMeta> {
        val currentFeed = feedMap[relayUrl].orEmpty()
        if (currentFeed.isEmpty()) {
            Log.i(TAG, "No feed for $relayUrl to append to")
            return listOf()
        }
        val newFeed = feedProvider.appendFeedByRelay(
            relayUrl = relayUrl,
            currentFeed = currentFeed,
            limit = BATCH_SIZE
        )
        feedMap[relayUrl] = newFeed
        val countOfNewPosts = newFeed.size - currentFeed.size
        require(countOfNewPosts >= 0)
        val appended = newFeed.takeLast(countOfNewPosts)
        viewModelState.value.let { state ->
            if (state.currentRelay == relayUrl) {
                viewModelState.update {
                    it.copy(posts = newFeed)
                }
            }
        }
        return appended
    }

    private fun setRefresh(value: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = value)
        }
    }

    private fun setRelayAndPosts(relayUrl: String, posts: List<PostWithMeta>) {
        feedMap.putIfAbsent(relayUrl, listOf())
        viewModelState.update {
            it.copy(currentRelay = relayUrl, posts = posts)
        }
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
