package com.kaiwolfram.nozzle.ui.app.views.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.preferences.profile.IProfileProvider
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
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
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "FeedViewModel"

data class FeedViewModelState(
    /**
     * Current posts sorted from oldest to newest
     */
    val posts: List<PostWithMeta> = mutableListOf(),
    val isRefreshing: Boolean = false,
    val pictureUrl: String = "",
    val pubkey: String = "",
)

class FeedViewModel(
    private val profileProvider: IProfileProvider,
    private val feedProvider: IFeedProvider,
    private val postCardInteractor: IPostCardInteractor,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(FeedViewModelState())
    private var isSyncing = AtomicBoolean(false)

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize FeedViewModel")
        viewModelState.update {
            it.copy(
                pictureUrl = profileProvider.getPictureUrl(),
                pubkey = profileProvider.getPubkey()
            )
        }

    }

    val onRefreshFeedView: () -> Unit = {
        execWhenSyncingNotBlocked {
            viewModelScope.launch(context = Dispatchers.IO) {
                Log.i(TAG, "Refresh feed view")
                setRefresh(true)
                updateFeed()
            }
        }
    }

    val onResetProfileIconUiState: () -> Unit = {
        viewModelState.update {
            it.copy(
                pictureUrl = profileProvider.getPictureUrl(),
                pubkey = profileProvider.getPubkey(),
            )
        }
    }

    val onLike: (String) -> Unit = { id ->
        uiState.value.let { state ->
            if (state.posts.any { post -> post.id == id }) {
                viewModelScope.launch(context = Dispatchers.IO) {
                    postCardInteractor.like(postId = id)
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

    private fun updateFeed() {
        Log.i(TAG, "Update feed")
        uiState.value.posts.let { posts ->
            if (posts.isEmpty()) {
                setInitialFeed()
            } else {
                addNewPostsToFeed()
            }
        }
    }

    private fun setInitialFeed() {
        Log.i(TAG, "Set initial feed")
        viewModelScope.launch(context = Dispatchers.IO) {
            viewModelState.update {
                it.copy(posts = feedProvider.getFeed(profileProvider.getPubkey()))
            }
        }
    }

    private fun addNewPostsToFeed() {
        Log.i(TAG, "Add new posts to feed")
        viewModelScope.launch(context = Dispatchers.IO) {
            val currentFeed = uiState.value.posts
            val newPosts = feedProvider.getFeedSince(
                pubkey = profileProvider.getPubkey(),
                since = currentFeed.first().createdAt
            )
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
    }

    private fun execWhenSyncingNotBlocked(exec: () -> Unit) {
        if (isSyncing.get()) {
            Log.i(TAG, "Blocked by active sync process")
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
            profileProvider: IProfileProvider,
            feedProvider: IFeedProvider,
            postCardInteractor: IPostCardInteractor,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(
                    profileProvider = profileProvider,
                    feedProvider = feedProvider,
                    postCardInteractor = postCardInteractor,
                ) as T
            }
        }
    }
}
