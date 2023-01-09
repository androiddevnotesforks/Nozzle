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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            delay(1000)
            setFeed()
            setRefresh(false)
        }
    }

    val onRefreshFeedView: () -> Unit = {
        execWhenNoActiveRefresh {
            viewModelScope.launch(context = IO) {
                Log.i(TAG, "Refresh feed view")
                setRefresh(true)
                setFeed()
                renewSubscriptions()
                setRefresh(false)
            }
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
        subscribeToAdditionalFeedData()
    }

    private suspend fun subscribeToFeed() {
        Log.i(TAG, "Subscribe to feed")
        val pubkeys = mutableListOf(personalProfileProvider.getPubkey())
        pubkeys.addAll(
            contactDao.listContactPubkeys(
                pubkey = personalProfileProvider.getPubkey()
            )
        )
        nostrSubscriber.unsubscribeFeeds()
        nostrSubscriber.subscribeToFeed(
            contactPubkeys = pubkeys,
            since = null
        )
    }

    private suspend fun subscribeToAdditionalFeedData() {
        Log.i(TAG, "Subscribe to additional feed data")
        val posts = feedProvider.getFeed()
        nostrSubscriber.unsubscribeAdditionalPostsData()

        nostrSubscriber.subscribeToAdditionalPostsData(
            postIds = listPostIds(posts),
            involvedPubkeys = listInvolvedPubkeys(posts),
            referencedPostIds = listReferencedPostIds(posts)
        )
    }

    private suspend fun setFeed() {
        Log.i(TAG, "Set feed")
        viewModelState.update {
            it.copy(posts = feedProvider.getFeed())
        }
    }

    private fun execWhenNoActiveRefresh(exec: () -> Unit) {
        if (uiState.value.isRefreshing) {
            Log.i(TAG, "Blocked by active refresh process")
        } else {
            exec()
        }
    }

    private fun setRefresh(value: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = value)
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
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
                    contactDao = contactDao
                ) as T
            }
        }
    }
}
