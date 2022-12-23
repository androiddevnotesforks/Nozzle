package com.kaiwolfram.nozzle.ui.app.views.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.preferences.profile.IProfileProvider
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
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

private const val TAG = "FeedViewModel"

data class FeedViewModelState(
    val posts: List<PostWithMeta> = listOf(),
    val isRefreshing: Boolean = false,
    val pictureUrl: String = "",
    val pubkey: String = "",
)

class FeedViewModel(
    private val nostrService: INostrService,
    private val postCardInteractor: IPostCardInteractor,
    private val profileProvider: IProfileProvider,
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
                fetchAndUseNostrData()
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
                    postCardInteractor.like(pubkey = uiState.value.pubkey, postId = id)
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
                    postCardInteractor.repost(pubkey = uiState.value.pubkey, postId = id)
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

    private fun fetchAndUseNostrData() {
        Log.i(TAG, "Fetching nostr data for feed")
        isSyncing.set(true)
        val posts = nostrService.listPosts()
        viewModelState.update {
            it.copy(
                posts = posts.map { post ->
                    PostWithMeta(
                        name = UUID.randomUUID().toString(),
                        id = UUID.randomUUID().toString(),
                        replyToId = UUID.randomUUID().toString(),
                        replyToName = "Kai Wolfram",
                        pubkey = UUID.randomUUID().toString(),
                        pictureUrl = "https://www.dadant.com/wp-content/uploads/2016/12/honey-production-dadant.jpg",
                        createdAt = post.createdAt,
                        content = post.content,
                        isLikedByMe = Random.nextBoolean(),
                        isRepostedByMe = Random.nextBoolean(),
                    )
                },
            )
        }
        isSyncing.set(false)
        setRefresh(false)
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
            nostrService: INostrService,
            postCardInteractor: IPostCardInteractor,
            profileProvider: IProfileProvider,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(
                    nostrService = nostrService,
                    postCardInteractor = postCardInteractor,
                    profileProvider = profileProvider,
                ) as T
            }
        }
    }
}
