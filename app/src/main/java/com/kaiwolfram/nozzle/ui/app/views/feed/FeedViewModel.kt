package com.kaiwolfram.nozzle.ui.app.views.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrRepository
import com.kaiwolfram.nozzle.data.preferences.PersonalProfileStorageReader
import com.kaiwolfram.nozzle.data.utils.mapToLikedPost
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
    private val nostrRepository: INostrRepository,
    private val profileStorageReader: PersonalProfileStorageReader,
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
                pictureUrl = profileStorageReader.getPictureUrl(),
                pubkey = profileStorageReader.getPubkey()
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
                pictureUrl = profileStorageReader.getPictureUrl(),
                pubkey = profileStorageReader.getPubkey(),
            )
        }
    }

    val onLike: (String) -> Unit = { id ->
//        TODO:
//        viewModelScope.launch(context = Dispatchers.IO) {
//            // Update db
//            // Send nostr event
//        }
        uiState.value.let { state ->
            if (state.posts.any { post -> post.id == id }) {
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

    private fun fetchAndUseNostrData() {
        Log.i(TAG, "Fetching nostr data for feed")
        isSyncing.set(true)
        val posts = nostrRepository.listPosts()
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
            nostrRepository: INostrRepository,
            profileStorageReader: PersonalProfileStorageReader,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(
                    nostrRepository = nostrRepository,
                    profileStorageReader = profileStorageReader
                ) as T
            }
        }
    }
}
