package com.kaiwolfram.nozzle.ui.app.views.thread

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.provider.IThreadProvider
import com.kaiwolfram.nozzle.data.utils.mapToLikedPost
import com.kaiwolfram.nozzle.data.utils.mapToRepostedPost
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.model.ThreadPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "ThreadViewModel"

data class ThreadViewModelState(
    val previous: List<PostWithMeta> = listOf(),
    val current: PostWithMeta? = null,
    val replies: List<PostWithMeta> = listOf(),
    val currentThreadPosition: ThreadPosition = ThreadPosition.SINGLE,
    val isRefreshing: Boolean = false,
)
class ThreadViewModel(
    private val threadProvider: IThreadProvider,
    private val postCardInteractor: IPostCardInteractor,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ThreadViewModelState())
    private var isSyncing = AtomicBoolean(false)
    private var currentEventId = ""

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize ThreadViewModel")
    }

    val onOpenThread: (String) -> Unit = { id ->
        Log.i(TAG, "Open thread of post $id")
        currentEventId = id
        onRefreshThreadView()
    }

    val onRefreshThreadView: () -> Unit = {
        execWhenSyncingNotBlocked {
            viewModelScope.launch(context = Dispatchers.IO) {
                Log.i(TAG, "Refresh thread view")
                setRefresh(true)
                val thread = threadProvider.getThread(currentEventId)
                viewModelState.update {
                    it.copy(
                        previous = thread.previous,
                        current = thread.current,
                        replies = thread.replies,
                        currentThreadPosition = getThreadPosition(thread.previous)
                    )
                }
                setRefresh(false)
                isSyncing.set(false)
            }
        }
    }

    val onLike: (String) -> Unit = { postId ->
        var needsUpdate = false
        uiState.value.let { state ->
            if (state.current != null && state.current.id == postId) {
                needsUpdate = true
                viewModelState.update {
                    it.copy(
                        current = state.current.copy(isLikedByMe = true),
                    )
                }
            } else if (state.previous.any { post -> post.id == postId }) {
                needsUpdate = true
                viewModelState.update {
                    it.copy(
                        previous = state.previous.map { toMap ->
                            mapToLikedPost(toMap = toMap, id = postId)
                        },
                    )
                }
            } else if (state.replies.any { post -> post.id == postId }) {
                needsUpdate = true
                viewModelState.update {
                    it.copy(
                        replies = state.replies.map { toMap ->
                            mapToLikedPost(toMap = toMap, id = postId)
                        },
                    )
                }
            }
        }
        if (needsUpdate) {
            viewModelScope.launch(context = Dispatchers.IO) {
                postCardInteractor.like(postId = postId, postPubkey = "FIXME")
            }
        }
    }

    val onRepost: (String) -> Unit = { postId ->
        var needsUpdate = false
        uiState.value.let { state ->
            if (state.current != null && state.current.id == postId) {
                needsUpdate = true
                viewModelState.update {
                    it.copy(
                        current = state.current.copy(isRepostedByMe = true),
                    )
                }
            } else if (state.previous.any { post -> post.id == postId }) {
                needsUpdate = true
                viewModelState.update {
                    it.copy(
                        previous = state.previous.map { toMap ->
                            mapToRepostedPost(toMap = toMap, id = postId)
                        },
                    )
                }
            } else if (state.replies.any { post -> post.id == postId }) {
                needsUpdate = true
                viewModelState.update {
                    it.copy(
                        replies = state.replies.map { toMap ->
                            mapToRepostedPost(toMap = toMap, id = postId)
                        },
                    )
                }
            }
        }
        if (needsUpdate) {
            viewModelScope.launch(context = Dispatchers.IO) {
                postCardInteractor.repost(postId = postId)
            }
        }
    }

    private fun getThreadPosition(previous: List<PostWithMeta>): ThreadPosition {
        return if (previous.isEmpty())
            ThreadPosition.SINGLE
        else {
            ThreadPosition.END
        }
    }

    private fun setRefresh(value: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = value)
        }
    }

    private fun execWhenSyncingNotBlocked(exec: () -> Unit) {
        if (isSyncing.get()) {
            Log.i(TAG, "Blocked by active sync process")
        } else {
            isSyncing.set(true)
            exec()
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        fun provideFactory(
            threadProvider: IThreadProvider,
            postCardInteractor: IPostCardInteractor,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ThreadViewModel(
                    threadProvider = threadProvider,
                    postCardInteractor = postCardInteractor,
                ) as T
            }
        }
    }
}
