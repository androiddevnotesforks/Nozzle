package com.kaiwolfram.nozzle.ui.app.views.feed

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

private const val TAG = "FeedViewModel"

data class FeedViewModelState(
    val posts: List<PostWithMeta> = listOf(),
    val isRefreshing: Boolean = false,
    val profilePicture: Painter = emptyPainter,
)

class FeedViewModel(
    private val nostrRepository: INostrRepository,
    private val defaultProfilePicture: Painter,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(FeedViewModelState())
    private var isSyncing = false

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
                profilePicture = defaultProfilePicture
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

    private fun fetchAndUseNostrData() {
        Log.i(TAG, "Fetching nostr data for feed")
        isSyncing = true
        val posts = nostrRepository.listPosts()
        viewModelState.update {
            it.copy(
                posts = posts.map { post ->
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
                },
            )
        }
        isSyncing = false
        setRefresh(false)
    }

    private fun execWhenSyncingNotBlocked(exec: () -> Unit) {
        if (isSyncing) {
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

    companion object {
        fun provideFactory(
            nostrRepository: INostrRepository,
            defaultProfilePicture: Painter,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(
                    nostrRepository = nostrRepository,
                    defaultProfilePicture = defaultProfilePicture
                ) as T
            }
        }
    }
}
