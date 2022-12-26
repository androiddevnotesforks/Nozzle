package com.kaiwolfram.nozzle.ui.app.views.thread

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.preferences.key.IPubkeyProvider
import com.kaiwolfram.nozzle.data.utils.derivePubkey
import com.kaiwolfram.nozzle.data.utils.generatePrivkey
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
    private val nostrRepository: INostrService,
    private val pubkeyProvider: IPubkeyProvider,
    private val postCardInteractor: IPostCardInteractor,
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
                        pubkey = derivePubkey(generatePrivkey()),
                        createdAt = post.createdAt,
                        content = post.content,
                        isLikedByMe = Random.nextBoolean(),
                        isRepostedByMe = Random.nextBoolean(),
                    )
                }
                val current = PostWithMeta(
                    name = UUID.randomUUID().toString(),
                    id = UUID.randomUUID().toString(),
                    replyToId = UUID.randomUUID().toString(),
                    pictureUrl = "https://avatars.githubusercontent.com/u/48265657?v=4",
                    replyToName = "Kai Wolfram",
                    pubkey = derivePubkey(generatePrivkey()),
                    createdAt = 66666666,
                    content = "post.content",
                    isLikedByMe = Random.nextBoolean(),
                    isRepostedByMe = Random.nextBoolean(),
                )
                val replies = nostrRepository.listPosts().map { post ->
                    PostWithMeta(
                        name = UUID.randomUUID().toString(),
                        id = UUID.randomUUID().toString(),
                        replyToId = UUID.randomUUID().toString(),
                        pictureUrl = "https://avatars.githubusercontent.com/u/48265657?v=4",
                        replyToName = "Kai Wolfram",
                        pubkey = derivePubkey(generatePrivkey()),
                        createdAt = post.createdAt,
                        content = post.content,
                        isLikedByMe = Random.nextBoolean(),
                        isRepostedByMe = Random.nextBoolean(),
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
                    pubkey = derivePubkey(generatePrivkey()),
                    createdAt = post.createdAt,
                    content = post.content,
                    isRepostedByMe = Random.nextBoolean(),
                    isLikedByMe = Random.nextBoolean(),
                )
            }
            val current = PostWithMeta(
                name = UUID.randomUUID().toString(),
                id = UUID.randomUUID().toString(),
                replyToId = UUID.randomUUID().toString(),
                replyToName = "Kai Wolfram",
                pictureUrl = "https://avatars.githubusercontent.com/u/48265657?v=4",
                pubkey = derivePubkey(generatePrivkey()),
                createdAt = 66666666,
                content = UUID.randomUUID().toString(),
                isRepostedByMe = Random.nextBoolean(),
                isLikedByMe = Random.nextBoolean(),
            )
            val replies = nostrRepository.listPosts().map { post ->
                PostWithMeta(
                    name = UUID.randomUUID().toString(),
                    id = UUID.randomUUID().toString(),
                    replyToId = UUID.randomUUID().toString(),
                    pictureUrl = "https://avatars.githubusercontent.com/u/48265657?v=4",
                    replyToName = "Kai Wolfram",
                    pubkey = derivePubkey(generatePrivkey()),
                    createdAt = post.createdAt,
                    content = post.content,
                    isLikedByMe = Random.nextBoolean(),
                    isRepostedByMe = Random.nextBoolean(),
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
        // TODO: This sucks lol
        var needsUpdate = false
        uiState.value.let { state ->
            if (state.current != null && state.current.id == id) {
                needsUpdate = true
                viewModelState.update {
                    it.copy(
                        current = state.current.copy(isLikedByMe = true),
                    )
                }
            } else if (state.previous.any { post -> post.id == id }) {
                needsUpdate = true
                viewModelState.update {
                    it.copy(
                        previous = state.previous.map { toMap ->
                            mapToLikedPost(toMap = toMap, id = id)
                        },
                    )
                }
            } else if (state.replies.any { post -> post.id == id }) {
                needsUpdate = true
                viewModelState.update {
                    it.copy(
                        replies = state.replies.map { toMap ->
                            mapToLikedPost(toMap = toMap, id = id)
                        },
                    )
                }
            }
        }
        if (needsUpdate) {
            viewModelScope.launch(context = Dispatchers.IO) {
                postCardInteractor.like(pubkey = pubkeyProvider.getPubkey(), postId = id)
            }
        }
    }

    val onRepost: (String) -> Unit = { id ->
        // TODO: This sucks lol
        var needsUpdate = false
        uiState.value.let { state ->
            if (state.current != null && state.current.id == id) {
                needsUpdate = true
                viewModelState.update {
                    it.copy(
                        current = state.current.copy(isRepostedByMe = true),
                    )
                }
            } else if (state.previous.any { post -> post.id == id }) {
                needsUpdate = true
                viewModelState.update {
                    it.copy(
                        previous = state.previous.map { toMap ->
                            mapToRepostedPost(toMap = toMap, id = id)
                        },
                    )
                }
            } else if (state.replies.any { post -> post.id == id }) {
                needsUpdate = true
                viewModelState.update {
                    it.copy(
                        replies = state.replies.map { toMap ->
                            mapToRepostedPost(toMap = toMap, id = id)
                        },
                    )
                }
            }
        }
        if (needsUpdate) {
            viewModelScope.launch(context = Dispatchers.IO) {
                postCardInteractor.repost(pubkey = pubkeyProvider.getPubkey(), postId = id)
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
            nostrService: INostrService,
            pubkeyProvider: IPubkeyProvider,
            postCardInteractor: IPostCardInteractor,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ThreadViewModel(
                    nostrRepository = nostrService,
                    pubkeyProvider = pubkeyProvider,
                    postCardInteractor = postCardInteractor,
                ) as T
            }
        }
    }
}
