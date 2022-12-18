package com.kaiwolfram.nozzle.ui.app.views.thread

import android.util.Log
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrRepository
import com.kaiwolfram.nozzle.data.utils.emptyPainter
import com.kaiwolfram.nozzle.model.PostWithMeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "ThreadViewModel"

private val emptyPost = PostWithMeta(
    id = "",
    replyToId = "",
    replyToName = "",
    name = "",
    picture = emptyPainter,
    pubkey = "",
    createdAt = -1,
    content = "",
)

data class ThreadViewModelState(
    val previous: List<PostWithMeta> = listOf(),
    val current: PostWithMeta = emptyPost,
    val replies: List<PostWithMeta> = listOf(),
    val isRefreshing: Boolean = false,
)

class ThreadViewModel(
    private val nostrRepository: INostrRepository,
    private val defaultProfilePicture: Painter,
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
        viewModelState.update {
            val current = uiState.value.current
            it.copy(
                current = current.copy(picture = defaultProfilePicture),
            )
        }
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
                        replyToId = UUID.randomUUID().toString(),
                        replyToName = "Kai Wolfram",
                        picture = defaultProfilePicture,
                        pubkey = UUID.randomUUID().toString(),
                        createdAt = post.createdAt,
                        content = post.content
                    )
                }
                val current = PostWithMeta(
                    name = UUID.randomUUID().toString(),
                    id = UUID.randomUUID().toString(),
                    replyToId = UUID.randomUUID().toString(),
                    replyToName = "Kai Wolfram",
                    picture = defaultProfilePicture,
                    pubkey = UUID.randomUUID().toString(),
                    createdAt = 66666666,
                    content = "post.content"
                )
                val replies = nostrRepository.listPosts().map { post ->
                    PostWithMeta(
                        name = UUID.randomUUID().toString(),
                        id = UUID.randomUUID().toString(),
                        replyToId = UUID.randomUUID().toString(),
                        replyToName = "Kai Wolfram",
                        picture = defaultProfilePicture,
                        pubkey = UUID.randomUUID().toString(),
                        createdAt = post.createdAt,
                        content = post.content
                    )
                }
                viewModelState.update {
                    it.copy(
                        previous = previous,
                        current = current,
                        replies = replies,
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
                    picture = defaultProfilePicture,
                    pubkey = UUID.randomUUID().toString(),
                    createdAt = post.createdAt,
                    content = post.content
                )
            }
            val current = PostWithMeta(
                name = UUID.randomUUID().toString(),
                id = UUID.randomUUID().toString(),
                replyToId = UUID.randomUUID().toString(),
                replyToName = "Kai Wolfram",
                picture = defaultProfilePicture,
                pubkey = UUID.randomUUID().toString(),
                createdAt = 66666666,
                content = "post.content"
            )
            val replies = nostrRepository.listPosts().map { post ->
                PostWithMeta(
                    name = UUID.randomUUID().toString(),
                    id = UUID.randomUUID().toString(),
                    replyToId = UUID.randomUUID().toString(),
                    replyToName = "Kai Wolfram",
                    picture = defaultProfilePicture,
                    pubkey = UUID.randomUUID().toString(),
                    createdAt = post.createdAt,
                    content = post.content
                )
            }
            Log.i(TAG, "Previous: ${previous.size}, replies: ${replies.size}")
            viewModelState.update {
                it.copy(
                    previous = previous,
                    current = current,
                    replies = replies,
                )
            }
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

    companion object {
        fun provideFactory(
            nostrRepository: INostrRepository,
            defaultProfilePicture: Painter,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ThreadViewModel(
                    nostrRepository = nostrRepository,
                    defaultProfilePicture = defaultProfilePicture
                ) as T
            }
        }
    }
}
