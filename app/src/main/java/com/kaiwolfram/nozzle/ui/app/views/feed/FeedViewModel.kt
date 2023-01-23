package com.kaiwolfram.nozzle.ui.app.views.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.provider.IPersonalProfileProvider
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.utils.*
import com.kaiwolfram.nozzle.model.PostWithMeta
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "FeedViewModel"

data class FeedViewModelState(
    /**
     * Current posts sorted from newest to oldest
     */
    val posts: List<PostWithMeta> = mutableListOf(),
    val isRefreshing: Boolean = false,
    val pubkey: String = "",
)

class FeedViewModel(
    private val personalProfileProvider: IPersonalProfileProvider,
    private val feedProvider: IFeedProvider,
    private val postCardInteractor: IPostCardInteractor,
    private val nostrSubscriber: INostrSubscriber,
    private val contactDao: ContactDao,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(FeedViewModelState())
    private val batchSize = 25

    var metadataState = personalProfileProvider.getMetadata()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            null
        )

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize FeedViewModel")
        viewModelScope.launch(context = IO) {
            setRefresh(true)
            renewSubscriptions()
            viewModelState.update {
                it.copy(pubkey = personalProfileProvider.getPubkey())
            }
            setFeed()
            setRefresh(false)
        }
    }

    val onRefreshFeedView: () -> Unit = {
        viewModelScope.launch(context = IO) {
            Log.i(TAG, "Refresh feed view")
            setRefresh(true)
            setFeed()
            renewSubscriptions()
            setRefresh(false)
        }
    }

    val onLoadMore: () -> Unit = {
        viewModelScope.launch(context = IO) {
            Log.i(TAG, "Load more")
            fetchAndAppendFeed()
        }
    }

    val onResetProfileIconUiState: () -> Unit = {
        Log.i(TAG, "Reset profile icon")
        metadataState = personalProfileProvider.getMetadata()
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                null
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

    private suspend fun renewSubscriptions() {
        subscribeToFeed()
        delay(1000)
        val posts = feedProvider.getFeed(limit = batchSize)
        subscribeToAdditionalFeedData(posts)
        delay(2500)
    }

    private suspend fun subscribeToFeed(until: Long? = null) {
        Log.i(TAG, "Subscribe to feed")
        nostrSubscriber.unsubscribeFeeds()
        val pubkeys = mutableListOf(personalProfileProvider.getPubkey())
        pubkeys.addAll(
            contactDao.listContactPubkeys(
                pubkey = personalProfileProvider.getPubkey()
            )
        )
        nostrSubscriber.subscribeToFeed(
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

    private suspend fun setFeed() {
        Log.i(TAG, "Set feed")
        viewModelState.update {
            it.copy(posts = feedProvider.getFeed(limit = batchSize))
        }
    }

    private val isAppending = AtomicBoolean(false)

    private suspend fun fetchAndAppendFeed() {
        if (isAppending.get()) return

        Log.i(TAG, "Append feed")
        viewModelState.value.let { state ->
            state.posts.lastOrNull()?.let { last ->
                isAppending.set(true)
                subscribeToFeed(until = last.createdAt)
                delay(1000)
                val newPosts = appendFeedAndGetNewPosts()
                subscribeToAdditionalFeedData(newPosts)
                isAppending.set(false)
            }
        }
    }

    private suspend fun appendFeedAndGetNewPosts(): List<PostWithMeta> {
        viewModelState.value.let { state ->
            val newFeed = feedProvider.appendFeed(currentFeed = state.posts, limit = batchSize)
            val countOfNewPosts = newFeed.size - state.posts.size
            require(countOfNewPosts >= 0)
            val appended = newFeed.takeLast(countOfNewPosts)
            viewModelState.update {
                it.copy(posts = newFeed)
            }
            return appended
        }
    }

    private fun setRefresh(value: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = value)
        }
    }

    companion object {
        fun provideFactory(
            personalProfileProvider: IPersonalProfileProvider,
            feedProvider: IFeedProvider,
            postCardInteractor: IPostCardInteractor,
            nostrSubscriber: INostrSubscriber,
            contactDao: ContactDao,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(
                    personalProfileProvider = personalProfileProvider,
                    feedProvider = feedProvider,
                    postCardInteractor = postCardInteractor,
                    nostrSubscriber = nostrSubscriber,
                    contactDao = contactDao,
                ) as T
            }
        }
    }
}
