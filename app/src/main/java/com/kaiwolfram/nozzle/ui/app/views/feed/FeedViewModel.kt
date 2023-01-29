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
private const val DB_BATCH_SIZE = 25
private const val SUB_BATCH_SIZE = 50

data class FeedViewModelState(
    /**
     * Current posts sorted from newest to oldest
     */
    val currentRelay: String = "",
    val feedMap: Map<String, List<PostWithMeta>> = mutableMapOf(),
    val isRefreshing: Boolean = false,
    val pubkey: String = "",
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
            renewAllSubscriptions(subBatchSize = SUB_BATCH_SIZE)
            val relays = relayProvider.listRelays()
            Log.i(TAG, "Listed ${relays.size} relays")
            viewModelState.update {
                it.copy(currentRelay = relays.firstOrNull().orEmpty())
            }
            setAllFeeds(
                relayUrls = relays,
                feedMap = uiState.value.feedMap,
                dbBatchSize = DB_BATCH_SIZE
            )
            setUIRefresh(false)
        }
    }

    val onRefreshFeedView: () -> Unit = {
        viewModelScope.launch(context = IO) {
            Log.i(TAG, "Refresh feed view")
            setUIRefresh(true)
            renewAllSubscriptions(subBatchSize = SUB_BATCH_SIZE)
            setAllFeeds(
                relayUrls = relayProvider.listRelays(),
                feedMap = uiState.value.feedMap,
                dbBatchSize = DB_BATCH_SIZE
            )
            setUIRefresh(false)
        }
    }

    val onLoadMore: () -> Unit = {
        viewModelScope.launch(context = IO) {
            Log.i(TAG, "Load more")
            fetchAndAppendFeedByRelay(
                relayUrl = uiState.value.currentRelay,
                feedMap = uiState.value.feedMap,
                subBatchSize = SUB_BATCH_SIZE,
                dbBatchSize = DB_BATCH_SIZE,
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
        setUIRelay(relayUrl = newRelayUrl)
    }

    val onNextHeadline: () -> Unit = {
        val relayUrls = relayProvider.listRelays()
        val newRelayUrl = when (val currentIndex = relayUrls.indexOf(uiState.value.currentRelay)) {
            in 0 until relayUrls.size - 1 -> relayUrls[currentIndex + 1]
            else -> relayUrls.first()
        }
        Log.i(TAG, "Next relay is $newRelayUrl")
        setUIRelay(relayUrl = newRelayUrl)
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
    // TODO: Move like, repost and follow logic to UI

    val onLike: (String) -> Unit = { id ->
        uiState.value.let { state ->
            state.feedMap[state.currentRelay]?.find { it.id == id }?.let {
                viewModelScope.launch(context = IO) {
                    postCardInteractor.like(postId = id, postPubkey = it.pubkey)
                }
//                viewModelState.update {
//                    val feedMap = state.feedMap.toMutableMap()
//                    feedMap[state.currentRelay] =
//                        feedMap[state.currentRelay].orEmpty().map { toMap ->
//                            mapToLikedPost(toMap = toMap, id = id)
//                        }
//                    it.copy(feedMap = feedMap)
//                }
            }
        }
    }

    val onRepost: (String) -> Unit = { id ->
        uiState.value.let { state ->
            if (state.feedMap[state.currentRelay].orEmpty().any { post -> post.id == id }) {
                viewModelScope.launch(context = IO) {
                    postCardInteractor.repost(postId = id)
                }
//                viewModelState.update {
//                    val feedMap = state.feedMap.toMutableMap()
//                    feedMap[state.currentRelay] =
//                        feedMap[state.currentRelay].orEmpty().map { toMap ->
//                            mapToRepostedPost(toMap = toMap, id = id)
//                        }
//                    it.copy(feedMap = feedMap)
//                }
            }
        }
    }

    private suspend fun renewAllSubscriptions(subBatchSize: Int) {
        subscribeToAllFeeds(subBatchSize = subBatchSize)
        delay(1000)
        subscribeToAdditionalFeedData(feedProvider.getFeed(limit = subBatchSize))
        delay(1000)
    }

    private suspend fun subscribeToAllFeeds(subBatchSize: Int, until: Long? = null) {
        Log.i(TAG, "Subscribe to all feeds")
        nostrSubscriber.unsubscribeFeeds()
        val pubkeys = listContactPubkeysAndYourself()
        Log.i(TAG, "Found ${pubkeys.size} contact pubkeys")
        nostrSubscriber.subscribeToFeed(
            authorPubkeys = pubkeys,
            limit = subBatchSize,
            until = until
        )
    }

    private suspend fun subscribeToFeedByRelay(
        relayUrl: String,
        batchSize: Int,
        until: Long? = null
    ) {
        Log.i(TAG, "Subscribe to feed of $relayUrl")
        nostrSubscriber.unsubscribeFeeds()
        val pubkeys = listContactPubkeysAndYourself()
        nostrSubscriber.subscribeToFeedByRelay(
            relayUrl = relayUrl,
            authorPubkeys = pubkeys,
            limit = batchSize,
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
        feedMap: Map<String, List<PostWithMeta>>,
        dbBatchSize: Int,
    ) {
        Log.i(TAG, "Set all feeds")
        val updatedMap = feedMap.toMutableMap()
        relayUrls.forEach { relayUrl ->
            val feedByRelay = feedProvider.getFeedByRelay(relayUrl = relayUrl, limit = dbBatchSize)
            updatedMap[relayUrl] = feedByRelay
            Log.i(TAG, "Feed for relay $relayUrl contains ${feedByRelay.size} posts")
        }
        setUIFeedMap(feedMap = updatedMap)
    }

    private val isAppending = AtomicBoolean(false)

    private suspend fun fetchAndAppendFeedByRelay(
        relayUrl: String,
        feedMap: Map<String, List<PostWithMeta>>,
        subBatchSize: Int,
        dbBatchSize: Int,
    ) {
        if (isAppending.get()) return

        Log.i(TAG, "Append feed for relay $relayUrl")
        feedMap[relayUrl].orEmpty().lastOrNull()?.let { last ->
            isAppending.set(true)
            subscribeToFeedByRelay(
                relayUrl = relayUrl,
                batchSize = subBatchSize,
                until = last.createdAt
            )
            delay(500)
            val newPosts = appendFeedAndGetNewPosts(
                relayUrl = relayUrl,
                feedMap = feedMap,
                dbBatchSize = dbBatchSize
            )
            subscribeToAdditionalFeedData(newPosts)
            isAppending.set(false)
        }
    }

    private suspend fun appendFeedAndGetNewPosts(
        relayUrl: String,
        feedMap: Map<String, List<PostWithMeta>>,
        dbBatchSize: Int,
    ): List<PostWithMeta> {
        val currentFeed = feedMap[relayUrl].orEmpty()
        if (currentFeed.isEmpty()) {
            Log.i(TAG, "No feed for $relayUrl to append to")
            return listOf()
        }
        val newFeed = feedProvider.appendFeedByRelay(
            relayUrl = relayUrl,
            currentFeed = currentFeed,
            limit = dbBatchSize
        )
        val countOfNewPosts = newFeed.size - currentFeed.size
        require(countOfNewPosts >= 0)

        val updatedMap = feedMap.toMutableMap()
        updatedMap[relayUrl] = newFeed
        setUIFeedMap(feedMap = updatedMap)

        return newFeed.takeLast(countOfNewPosts)
    }

    private fun setUIRefresh(value: Boolean) {
        viewModelState.update { it.copy(isRefreshing = value) }
    }

    private fun setUIRelay(relayUrl: String) {
        viewModelState.update { it.copy(currentRelay = relayUrl) }
    }

    private fun setUIFeedMap(feedMap: Map<String, List<PostWithMeta>>) {
        viewModelState.update { it.copy(feedMap = feedMap) }
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
