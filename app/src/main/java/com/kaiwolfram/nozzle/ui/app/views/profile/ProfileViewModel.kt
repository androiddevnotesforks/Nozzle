package com.kaiwolfram.nozzle.ui.app.views.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.profileFollower.IProfileFollower
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.provider.IProfileProvider
import com.kaiwolfram.nozzle.data.utils.hexToNpub
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

private const val TAG = "ProfileViewModel"

data class ProfileViewModelState(
    val pubkey: String = "",
    val npub: String = "",
    val name: String = "",
    val about: String = "",
    val picture: String = "",
    val numOfFollowing: Int = 0,
    val numOfFollowers: Int = 0,
    val isOneself: Boolean = true,
    val isFollowed: Boolean = false,
    val posts: List<PostWithMeta> = listOf(),
    val isRefreshing: Boolean = false,
)

// TODO: This whole viewmodel kinda sucks. Needs better implementation

class ProfileViewModel(
    private val nostrService: INostrService,
    private val feedProvider: IFeedProvider,
    private val profileProvider: IProfileProvider,
    private val profileFollower: IProfileFollower,
    private val postCardInteractor: IPostCardInteractor,
    context: Context,
    clip: ClipboardManager,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ProfileViewModelState())
    private var isSyncing = AtomicBoolean(false)

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize ProfileViewModel")
    }

    val onSetPubkey: (String) -> Unit = { pubkey ->
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.i(TAG, "Set UI data for $pubkey")
            // TODO: Unsubscribe on dispose
            nostrService.subscribeToProfileMetadataAndContactList(pubkey)
            useCachedValues(pubkey)
        }
    }

    val onCopyNpubAndShowToast: (String) -> Unit = { toast ->
        uiState.value.npub.let {
            Log.i(TAG, "Copy npub $it and show toast '$toast'")
            clip.setText(AnnotatedString(it))
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
        }
    }

    val onRefreshProfileView: () -> Unit = {
        execWhenSyncingNotBlocked {
            viewModelScope.launch(context = Dispatchers.IO) {
                Log.i(TAG, "Refresh profile view")
                setRefresh(true)
                useCachedValues(uiState.value.pubkey)
                isSyncing.set(false)
            }
        }
    }

    val onLike: (String) -> Unit = { id ->
        uiState.value.let { state ->
            state.posts.find { it.id == id }?.let {
                viewModelScope.launch(context = Dispatchers.IO) {
                    postCardInteractor.like(postId = id, postPubkey = it.pubkey)
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

    val onFollow: (String) -> Unit = { pubkeyToFollow ->
        // TODO: Set correct relayUrl
        if (!uiState.value.isFollowed) {
            viewModelScope.launch(context = Dispatchers.IO) {
                profileFollower.follow(pubkeyToFollow = pubkeyToFollow, relayUrl = "")
            }
            viewModelState.update {
                it.copy(isFollowed = true)
            }
        }
    }

    val onUnfollow: (String) -> Unit = { pubkeyToUnfollow ->
        if (uiState.value.isFollowed) {
            viewModelScope.launch(context = Dispatchers.IO) {
                profileFollower.unfollow(pubkeyToUnfollow = pubkeyToUnfollow)
            }
            viewModelState.update {
                it.copy(isFollowed = false)
            }
        }
    }

    private suspend fun useCachedValues(pubkey: String) {
        val cachedProfile = profileProvider.getProfile(pubkey)
        if (cachedProfile != null) {
            Log.i(TAG, "Use cached values")
            viewModelState.update {
                it.copy(
                    pubkey = pubkey,
                    npub = cachedProfile.npub,
                    name = cachedProfile.metadata.name.orEmpty(),
                    about = cachedProfile.metadata.about.orEmpty(),
                    picture = cachedProfile.metadata.picture.orEmpty(),
                    numOfFollowing = cachedProfile.numOfFollowing,
                    numOfFollowers = cachedProfile.numOfFollowers,
                    isOneself = cachedProfile.isOneself,
                    isFollowed = cachedProfile.isFollowedByMe,
                    posts = feedProvider.getFeedWithSingleAuthor(pubkey)
                )
            }
        } else {
            Log.i(TAG, "Reset UI state because cache is empty")
            resetValues(pubkey)
        }
        setRefresh(false)
    }

    private fun resetValues(pubkey: String) {
        Log.i(TAG, "Reset values for to $pubkey")
        viewModelState.update {
            val npub = hexToNpub(pubkey)
            it.copy(
                pubkey = pubkey,
                npub = npub,
                name = npub,
                about = "",
                picture = "",
                isFollowed = false,
                numOfFollowing = 0,
                numOfFollowers = 0,
                posts = listOf(),
            )
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
            profileFollower: IProfileFollower,
            postCardInteractor: IPostCardInteractor,
            feedProvider: IFeedProvider,
            profileProvider: IProfileProvider,
            context: Context,
            clip: ClipboardManager,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(
                        nostrService = nostrService,
                        profileFollower = profileFollower,
                        postCardInteractor = postCardInteractor,
                        feedProvider = feedProvider,
                        profileProvider = profileProvider,
                        context = context,
                        clip = clip,
                    ) as T
                }
            }
    }
}
