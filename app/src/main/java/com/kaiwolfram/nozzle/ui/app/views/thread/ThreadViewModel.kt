package com.kaiwolfram.nozzle.ui.app.views.thread

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrRepository
import com.kaiwolfram.nozzle.data.utils.mapToLikedPost
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.model.ThreadPosition
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

private const val TAG = "ThreadViewModel"

data class ThreadViewModelState(
    val previous: List<PostWithMeta> = listOf(),
    val current: PostWithMeta? = null,
    val replies: List<PostWithMeta> = listOf(),
    val currentThreadPosition: ThreadPosition = ThreadPosition.SINGLE,
    val isRefreshing: Boolean = false,
)

class ThreadViewModel(
    private val nostrRepository: INostrRepository,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ThreadViewModelState())
    private var isSyncing = AtomicBoolean(false)

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize ThreadViewModel")
    }

    val onRefreshThreadView: () -> Unit = {
        execWhenSyncingNotBlocked {
            viewModelScope.launch(context = Dispatchers.IO) {
                Log.i(TAG, "Refresh thread view")
                setRefresh(true)
                val previous = nostrRepository.listPosts().map { post ->
                    PostWithMeta(
                        name = UUID.randomUUID().toString(),
                        id = UUID.randomUUID().toString(),
                        pictureUrl = "https://avatars.githubusercontent.com/u/48265657?v=4",
                        replyToId = UUID.randomUUID().toString(),
                        replyToName = "Kai Wolfram",
                        pubkey = UUID.randomUUID().toString(),
                        createdAt = post.createdAt,
                        content = post.content,
                        isLikedByMe = Random.nextBoolean(),
                        numOfLikes = Random.nextInt(2000),
                    )
                }
                val current = PostWithMeta(
                    name = UUID.randomUUID().toString(),
                    id = UUID.randomUUID().toString(),
                    replyToId = UUID.randomUUID().toString(),
                    pictureUrl = "https://avatars.githubusercontent.com/u/48265657?v=4",
                    replyToName = "Kai Wolfram",
                    pubkey = UUID.randomUUID().toString(),
                    createdAt = 66666666,
                    content = "post.content",
                    isLikedByMe = Random.nextBoolean(),
                    numOfLikes = Random.nextInt(2000),
                )
                val replies = nostrRepository.listPosts().map { post ->
                    PostWithMeta(
                        name = UUID.randomUUID().toString(),
                        id = UUID.randomUUID().toString(),
                        replyToId = UUID.randomUUID().toString(),
                        pictureUrl = "https://avatars.githubusercontent.com/u/48265657?v=4",
                        replyToName = "Kai Wolfram",
                        pubkey = UUID.randomUUID().toString(),
                        createdAt = post.createdAt,
                        content = post.content,
                        isLikedByMe = Random.nextBoolean(),
                        numOfLikes = Random.nextInt(2000),
                    )
                }
                viewModelState.update {
                    it.copy(
                        previous = previous,
                        current = current,
                        replies = replies,
                        currentThreadPosition = getThreadPosition(previous)
                    )
                }
                setRefresh(false)
            }
        }
    }

    val onOpenThread: (String) -> Unit = { id ->
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.i(TAG, "Open thread of post $id")
            val previous = nostrRepository.listPosts().map { post ->
                PostWithMeta(
                    name = UUID.randomUUID().toString(),
                    id = UUID.randomUUID().toString(),
                    replyToId = UUID.randomUUID().toString(),
                    replyToName = "Kai Wolfram",
                    pictureUrl = "https://avatars.githubusercontent.com/u/48265657?v=4",
                    pubkey = UUID.randomUUID().toString(),
                    createdAt = post.createdAt,
                    content = post.content,
                    isLikedByMe = Random.nextBoolean(),
                    numOfLikes = Random.nextInt(2000),
                )
            }
            val current = PostWithMeta(
                name = UUID.randomUUID().toString(),
                id = UUID.randomUUID().toString(),
                replyToId = UUID.randomUUID().toString(),
                replyToName = "Kai Wolfram",
                pictureUrl = "https://avatars.githubusercontent.com/u/48265657?v=4",
                pubkey = UUID.randomUUID().toString(),
                createdAt = 66666666,
                content = UUID.randomUUID().toString(),
                isLikedByMe = Random.nextBoolean(),
                numOfLikes = Random.nextInt(2000),
            )
            val replies = nostrRepository.listPosts().map { post ->
                PostWithMeta(
                    name = UUID.randomUUID().toString(),
                    id = UUID.randomUUID().toString(),
                    replyToId = UUID.randomUUID().toString(),
                    pictureUrl = "https://avatars.githubusercontent.com/u/48265657?v=4",
                    replyToName = "Kai Wolfram",
                    pubkey = UUID.randomUUID().toString(),
                    createdAt = post.createdAt,
                    content = post.content,
                    isLikedByMe = Random.nextBoolean(),
                    numOfLikes = Random.nextInt(2000),
                )
            }
            Log.i(TAG, "Previous: ${previous.size}, replies: ${replies.size}")
            viewModelState.update {
                it.copy(
                    previous = previous,
                    current = current,
                    replies = replies,
                    currentThreadPosition = getThreadPosition(previous)
                )
            }
        }
    }

    val onLike: (String) -> Unit = { id ->
//        TODO:
//        viewModelScope.launch(context = Dispatchers.IO) {
//            // Update db
//            // Send nostr event
//        }
        // TODO: This sucks lol
        uiState.value.let { state ->
            if (state.current != null && state.current.id == id) {
                viewModelState.update {
                    it.copy(
                        current = state.current.copy(isLikedByMe = true),
                    )
                }
            } else if (state.previous.any { post -> post.id == id }) {
                viewModelState.update {
                    it.copy(
                        previous = state.previous.map { toMap ->
                            mapToLikedPost(toMap = toMap, id = id)
                        },
                    )
                }
            } else if (state.replies.any { post -> post.id == id }) {
                viewModelState.update {
                    it.copy(
                        replies = state.replies.map { toMap ->
                            mapToLikedPost(toMap = toMap, id = id)
                        },
                    )
                }
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
            exec()
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        fun provideFactory(
            nostrRepository: INostrRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ThreadViewModel(
                    nostrRepository = nostrRepository,
                ) as T
            }
        }
    }
}
