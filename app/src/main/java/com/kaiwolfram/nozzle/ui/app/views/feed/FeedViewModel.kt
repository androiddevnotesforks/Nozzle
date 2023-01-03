package com.kaiwolfram.nozzle.ui.app.views.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.provider.IPersonalProfileProvider
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.utils.mapToLikedPost
import com.kaiwolfram.nozzle.data.utils.mapToRepostedPost
import com.kaiwolfram.nozzle.model.PostWithMeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
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
    val pictureUrl: String = "",
    val pubkey: String = "",
)

class FeedViewModel(
    private val personalProfileProvider: IPersonalProfileProvider,
    private val feedProvider: IFeedProvider,
    private val postCardInteractor: IPostCardInteractor,
    private val nostrService: INostrService,
    private val contactDao: ContactDao,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(FeedViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize FeedViewModel")
        viewModelScope.launch(context = Dispatchers.IO) {
            viewModelState.update {
                it.copy(
                    // TODO: Write dao method for getting picture only
                    pictureUrl = personalProfileProvider.getMetadata()?.picture.orEmpty(),
                    pubkey = personalProfileProvider.getPubkey()
                )
            }
        }

        handleNostrSubscription()
        viewModelScope.launch(context = Dispatchers.IO) {
            setInitialFeed()
        }
    }

    val onRefreshFeedView: () -> Unit = {
        execWhenNoActiveRefresh {
            viewModelScope.launch(context = Dispatchers.IO) {
                Log.i(TAG, "Refresh feed view")
                setRefresh(true)
                updateFeed()
                setRefresh(false)
            }
        }
    }

    val onResetProfileIconUiState: () -> Unit = {
        viewModelScope.launch(context = Dispatchers.IO) {
            viewModelState.update {
                it.copy(
                    pictureUrl = personalProfileProvider.getMetadata()?.picture.orEmpty(),
                    pubkey = personalProfileProvider.getPubkey(),
                )
            }
        }
    }

    val onLike: (String) -> Unit = { id ->
        uiState.value.let { state ->
            state.posts.find { it.id == id }?.let {
                viewModelScope.launch(context = Dispatchers.IO) {
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
                viewModelScope.launch(context = Dispatchers.IO) {
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

    private fun handleNostrSubscription() {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.i(TAG, "Handle nostr subscription")
            nostrService.subscribeToFeed(
                contactPubkeys = contactDao.listContactPubkeys(
                    pubkey = personalProfileProvider.getPubkey()
                ),
                since = feedProvider.getLatestTimestamp()
            )
        }
    }

    private suspend fun updateFeed() {
        Log.i(TAG, "Update feed")
        if (uiState.value.posts.isEmpty()) {
            setInitialFeed()
        } else {
            addNewPostsToFeed()
        }
    }

    private suspend fun setInitialFeed() {
        Log.i(TAG, "Set initial feed")
        viewModelState.update {
            it.copy(posts = feedProvider.getFeed())
        }
    }

    private suspend fun addNewPostsToFeed() {
        Log.i(TAG, "Add new posts to feed")
        val currentFeed = uiState.value.posts
        val newPosts = feedProvider.getFeedSince(since = currentFeed.last().createdAt)
        Log.i(TAG, "Found ${newPosts.size} new posts to add to feed")
        if (newPosts.isNotEmpty()) {
            val newFeed = mutableListOf<PostWithMeta>()
            newFeed.addAll(currentFeed)
            newFeed.addAll(newPosts)
            viewModelState.update {
                it.copy(posts = newFeed)
            }
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
            nostrService: INostrService,
            contactDao: ContactDao,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(
                    personalProfileProvider = personalProfileProvider,
                    feedProvider = feedProvider,
                    postCardInteractor = postCardInteractor,
                    nostrService = nostrService,
                    contactDao = contactDao
                ) as T
            }
        }
    }
}
