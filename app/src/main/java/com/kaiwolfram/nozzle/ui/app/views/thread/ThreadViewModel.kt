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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "ThreadViewModel"
private const val WAIT_TIME = 2100L

class ThreadViewModel(
    private val threadProvider: IThreadProvider,
    private val postCardInteractor: IPostCardInteractor,
    private val nostrSubscriber: INostrSubscriber,
) : ViewModel() {
    private lateinit var currentPostIds: PostIds

    private val isRefreshing = MutableStateFlow(false)
    val isRefreshingState = isRefreshing
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            isRefreshing.value
        )

    var threadState: StateFlow<PostThread> = MutableStateFlow(PostThread.createEmpty())

    init {
        Log.i(TAG, "Initialize ThreadViewModel")
    }

    private var job: Job? = null
    val onOpenThread: (PostIds) -> Unit = { postIds ->
        Log.i(TAG, "Open thread of post ${postIds.id}")
        setEmptyThread()
        currentPostIds = postIds
        job?.let { if (it.isActive) it.cancel() }
        job = viewModelScope.launch(context = Dispatchers.IO) {
            setUIRefresh(true)
            updateScreen(postIds = currentPostIds)
            setUIRefresh(false)
            delay(WAIT_TIME)
            renewAdditionalDataSubscription(threadState.value)
            updateCurrentPostIds(threadState.value)
        }
    }

    val onRefreshThreadView: () -> Unit = {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.i(TAG, "Refresh thread view")
            setUIRefresh(true)
            updateScreen(
                postIds = currentPostIds,
                waitForSubscription = WAIT_TIME,
                initValue = threadState.value
            )
            setUIRefresh(false)
            delay(WAIT_TIME)
            renewAdditionalDataSubscription(threadState.value)
            updateCurrentPostIds(threadState.value)
        }
    }

    val onLike: (String) -> Unit = { postId ->
        val toLike = threadState.value.let { state ->
            if (state.current?.id == postId) {
                state.current
            } else if (state.previous.any { post -> post.id == postId }) {
                state.previous.find { post -> post.id == postId }
            } else if (state.replies.any { post -> post.id == postId }) {
                state.replies.find { post -> post.id == postId }
            } else {
                null
            }
        }
        toLike?.let {
            viewModelScope.launch(context = Dispatchers.IO) {
                // TODO: Use your write relays, and source relays (?)
                postCardInteractor.like(postId = postId, postPubkey = it.pubkey, relays = null)
            }
        }
    }

    val onRepost: (String) -> Unit = { postId ->
        viewModelScope.launch(context = Dispatchers.IO) {
            // TODO: Use your write relays, and source relays (?)
            postCardInteractor.repost(postId = postId, relays = null)
        }
    }

    private suspend fun updateScreen(
        postIds: PostIds,
        waitForSubscription: Long? = null,
        initValue: PostThread = PostThread.createEmpty()
    ) {
        threadState = threadProvider.getThreadFlow(
            currentPostId = postIds.id,
            replyToId = postIds.replyToId,
            waitForSubscription = waitForSubscription,
        )
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                initValue,
            )
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

    private suspend fun renewAdditionalDataSubscription(thread: PostThread) {
        nostrSubscriber.unsubscribeAdditionalPostsData()
        nostrSubscriber.subscribeToAdditionalPostsData(thread.getList())
    }

    private fun setEmptyThread() {
        threadState = MutableStateFlow(PostThread.createEmpty())
    }

    private fun setUIRefresh(value: Boolean) {
        isRefreshing.update { value }
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
