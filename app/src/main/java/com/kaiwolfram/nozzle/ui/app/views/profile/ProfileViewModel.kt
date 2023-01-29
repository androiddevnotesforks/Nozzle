package com.kaiwolfram.nozzle.ui.app.views.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.data.postCardInteractor.IPostCardInteractor
import com.kaiwolfram.nozzle.data.profileFollower.IProfileFollower
import com.kaiwolfram.nozzle.data.provider.IFeedProvider
import com.kaiwolfram.nozzle.data.provider.IProfileWithAdditionalInfoProvider
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.model.ProfileWithAdditionalInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "ProfileViewModel"
private const val DB_BATCH_SIZE = 25

class ProfileViewModel(
    private val feedProvider: IFeedProvider,
    private val profileProvider: IProfileWithAdditionalInfoProvider,
    private val profileFollower: IProfileFollower,
    private val postCardInteractor: IPostCardInteractor,
    context: Context,
    clip: ClipboardManager,
) : ViewModel() {
    private val isRefreshing = MutableStateFlow(false)
    val isRefreshingState = isRefreshing
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false
        )


    // TODO: Figure out how to do it without this hack
    private val forceRecomposition = MutableStateFlow(0)
    val forceRecompositionState = forceRecomposition
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            0
        )

    var profileState: StateFlow<ProfileWithAdditionalInfo> = MutableStateFlow(
        ProfileWithAdditionalInfo(
            pubkey = "",
            npub = "",
            metadata = Metadata(),
            numOfFollowing = 0,
            numOfFollowers = 0,
            numOfRelays = 0,
            isOneself = false,
            isFollowedByMe = false,
        )
    )

    var postsState: StateFlow<List<PostWithMeta>> = MutableStateFlow(listOf())

    init {
        Log.i(TAG, "Initialize ProfileViewModel")
    }

    val onSetPubkey: (String) -> Unit = { pubkey ->
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.i(TAG, "Set UI data for $pubkey")
            refreshProfileAndPostState(pubkey = pubkey, dbBatchSize = DB_BATCH_SIZE)
        }
    }

    val onRefreshProfileView: () -> Unit = {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.i(TAG, "Refresh profile view")
            setUIRefresh(true)
            refreshProfileAndPostState(
                pubkey = profileState.value.pubkey,
                dbBatchSize = DB_BATCH_SIZE
            )
            delay(1000)
            setUIRefresh(false)
        }
    }

    val onLoadMore: () -> Unit = {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.i(TAG, "Load more")
            postsState = feedProvider.appendFeedWithSingleAuthor(
                currentFeed = postsState.value,
                pubkey = profileState.value.pubkey,
                limit = DB_BATCH_SIZE,
            ).stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                postsState.value,
            )
            forceRecomposition.update { it + 1 }
        }
    }

    val onCopyNpub: () -> Unit = {
        profileState.value.npub.let {
            Log.i(TAG, "Copy npub $it")
            clip.setText(AnnotatedString(it))
            Toast.makeText(context, context.getString(R.string.pubkey_copied), Toast.LENGTH_SHORT)
                .show()
        }
    }

    val onLike: (String) -> Unit = { id ->
        postsState.value.find { it.id == id }?.let {
            viewModelScope.launch(context = Dispatchers.IO) {
                postCardInteractor.like(postId = id, postPubkey = it.pubkey)
            }
        }
    }

    val onRepost: (String) -> Unit = { id ->
        viewModelScope.launch(context = Dispatchers.IO) {
            postCardInteractor.repost(postId = id)
        }
    }

    val onFollow: (String) -> Unit = { pubkeyToFollow ->
        if (!profileState.value.isFollowedByMe) {
            viewModelScope.launch(context = Dispatchers.IO) {
                profileFollower.follow(pubkeyToFollow = pubkeyToFollow, relayUrl = "")
            }
        }
    }

    val onUnfollow: (String) -> Unit = { pubkeyToUnfollow ->
        if (profileState.value.isFollowedByMe) {
            viewModelScope.launch(context = Dispatchers.IO) {
                profileFollower.unfollow(pubkeyToUnfollow = pubkeyToUnfollow)
            }
        }
    }

    private fun refreshProfileAndPostState(pubkey: String, dbBatchSize: Int) {
        Log.i(TAG, "Refresh profile and posts of $pubkey")
        profileState = profileProvider.getProfile(pubkey).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            profileState.value,
        )
        postsState = feedProvider.getFeedWithSingleAuthor(
            pubkey = pubkey, limit = dbBatchSize
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            postsState.value,
        )
        forceRecomposition.update { it + 1 }
    }

    private fun setUIRefresh(value: Boolean) {
        isRefreshing.update { value }
    }

    companion object {
        fun provideFactory(
            profileFollower: IProfileFollower,
            postCardInteractor: IPostCardInteractor,
            feedProvider: IFeedProvider,
            profileProvider: IProfileWithAdditionalInfoProvider,
            context: Context,
            clip: ClipboardManager,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(
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
