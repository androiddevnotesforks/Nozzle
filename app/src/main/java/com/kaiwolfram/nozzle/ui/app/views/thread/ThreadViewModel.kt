package com.kaiwolfram.nozzle.ui.app.views.thread

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.provider.IThreadProvider
import com.kaiwolfram.nozzle.data.utils.*
import com.kaiwolfram.nozzle.model.PostIds
import com.kaiwolfram.nozzle.model.PostThread
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.model.ThreadPosition
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

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
    private val nostrSubscriber: INostrSubscriber,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ThreadViewModelState())
    private lateinit var currentPostIds: PostIds

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize ThreadViewModel")
    }

    private var job: Job? = null
    val onOpenThread: (PostIds) -> Unit = { postIds ->
        Log.i(TAG, "Open thread of post ${postIds.id}")
        currentPostIds = postIds
        setEmptyThread()
        job?.let { if (it.isActive) it.cancel() }
        job = viewModelScope.launch(context = Dispatchers.IO) {
            setThread(threadProvider.getThread(postIds))
            renewThreadSubscription()
            delay(1000)
            val thread = threadProvider.getThread(postIds)
            setThread(thread)
            setThreadWithNewData(thread)
            updateCurrentPostIds(thread)
        }
    }

    val onRefreshThreadView: () -> Unit = {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.i(TAG, "Refresh thread view")
            setRefresh(true)
            renewThreadSubscription()
            delay(1000)
            val thread = threadProvider.getThread(currentPostIds)
            renewAdditionalDataSubscription(thread)
            updateCurrentPostIds(thread)
            delay(1000)
            setThread(threadProvider.getThread(currentPostIds))
            setRefresh(false)
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

    private fun setThread(thread: PostThread) {
        Log.i(TAG, "Set thread ${thread.current?.id}")
        viewModelState.update {
            it.copy(
                previous = thread.previous,
                current = thread.current,
                replies = thread.replies,
                currentThreadPosition = getThreadPosition(thread.previous)
            )
        }
    }

    private fun setEmptyThread() {
        viewModelState.update {
            it.copy(
                previous = listOf(),
                current = null,
                replies = listOf(),
                currentThreadPosition = ThreadPosition.SINGLE
            )
        }
    }

    private fun updateCurrentPostIds(thread: PostThread) {
        thread.current?.let {
            currentPostIds = PostIds(
                id = it.id,
                replyToId = it.replyToId,
                replyToRootId = it.replyToRootId
            )
        }
    }

    private fun renewThreadSubscription() {
        nostrSubscriber.unsubscribeThread()
        nostrSubscriber.subscribeToThread(
            currentPostId = currentPostIds.id,
            replyToId = currentPostIds.replyToId,
            replyToRootId = currentPostIds.replyToRootId
        )
    }

    private fun renewAdditionalDataSubscription(thread: PostThread) {
        nostrSubscriber.unsubscribeAdditionalPostsData()
        nostrSubscriber.subscribeToAdditionalPostsData(
            postIds = listPostIds(thread),
            referencedPostIds = listReferencedPostIds(thread),
            referencedPubkeys = listReferencedPubkeys(thread)
        )
    }

    private suspend fun setThreadWithNewData(thread: PostThread) {
        renewAdditionalDataSubscription(thread)
        delay(1000)
        thread.current?.let {
            setThread(
                threadProvider.getThread(
                    PostIds(
                        id = it.id,
                        replyToRootId = it.replyToRootId,
                        replyToId = it.replyToId
                    )
                )
            )
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

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        fun provideFactory(
            threadProvider: IThreadProvider,
            postCardInteractor: IPostCardInteractor,
            nostrSubscriber: INostrSubscriber
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ThreadViewModel(
                    threadProvider = threadProvider,
                    postCardInteractor = postCardInteractor,
                    nostrSubscriber = nostrSubscriber,
                ) as T
            }
        }
    }
}
