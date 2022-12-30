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
import com.kaiwolfram.nozzle.data.preferences.key.IPubkeyProvider
import com.kaiwolfram.nozzle.data.profileFollower.IProfileFollower
import com.kaiwolfram.nozzle.data.room.dao.EventDao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.data.room.entity.ProfileEntity
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
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

private const val TAG = "ProfileViewModel"

data class ProfileViewModelState(
    val pubkey: String = "",
    val npub: String = "",
    val name: String = "",
    val bio: String = "",
    val pictureUrl: String = "",
    val numOfFollowing: Int = 0,
    val numOfFollowers: Int = 0,
    val isOneself: Boolean = true,
    val isFollowed: Boolean = false,
    val posts: List<PostWithMeta> = listOf(),
    val isRefreshing: Boolean = false,
)

class ProfileViewModel(
    private val nostrService: INostrService,
    private val profileDao: ProfileDao,
    private val eventDao: EventDao,
    private val profileFollower: IProfileFollower,
    private val postCardInteractor: IPostCardInteractor,
    private val pubkeyProvider: IPubkeyProvider,
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
            Log.i(TAG, "Setting ui data for $pubkey")
            useCachedValues(pubkey)
            fetchAndUseNostrData(pubkey)
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
                fetchAndUseNostrData(uiState.value.pubkey)
            }
        }
    }

    val onLike: (String) -> Unit = { id ->
        uiState.value.let { state ->
            if (state.posts.any { post -> post.id == id }) {
                viewModelScope.launch(context = Dispatchers.IO) {
                    postCardInteractor.like(pubkey = uiState.value.pubkey, postId = id)
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
                    postCardInteractor.repost(pubkey = uiState.value.pubkey, postId = id)
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
        if (!uiState.value.isFollowed) {
            viewModelScope.launch(context = Dispatchers.IO) {
                profileFollower.follow(
                    pubkey = pubkeyProvider.getPubkey(),
                    pubkeyToFollow = pubkeyToFollow
                )
            }
            viewModelState.update {
                it.copy(isFollowed = true)
            }
        }
    }

    val onUnfollow: (String) -> Unit = { pubkeyToUnfollow ->
        if (uiState.value.isFollowed) {
            viewModelScope.launch(context = Dispatchers.IO) {
                profileFollower.unfollow(
                    pubkey = pubkeyProvider.getPubkey(),
                    pubkeyToUnfollow = pubkeyToUnfollow
                )
            }
            viewModelState.update {
                it.copy(isFollowed = false)
            }
        }
    }

    private suspend fun fetchAndUseNostrData(pubkey: String) {
        Log.i(TAG, "Fetching nostr data for $pubkey")
        isSyncing.set(true)
        val nostrProfile = nostrService.getProfile(pubkey)
        if (nostrProfile != null) {
            val numOfFollowing = nostrService.getFollowingCount(pubkey)
            val numOfFollowers = nostrService.getFollowerCount(pubkey)
            val profile = ProfileEntity(
                pubkey = pubkey,
                name = nostrProfile.name,
                about = nostrProfile.about,
                picture = nostrProfile.picture,
                numOfFollowing = numOfFollowing,
                numOfFollowers = numOfFollowers,
            )
            val posts = nostrService.listPosts(pubkey)
            useAndCacheProfile(profile = profile, posts = posts)
        }
        isSyncing.set(false)
        setRefresh(false)
    }

    private suspend fun useAndCacheProfile(
        profile: ProfileEntity,
        posts: List<PostEntity>,
    ) {
        Log.i(TAG, "Caching fetched profile of ${profile.pubkey}")
        profileDao.insert(profile)
        eventDao.insert(posts)
        viewModelState.update {
            it.copy(
                pubkey = profile.pubkey,
                npub = hexToNpub(profile.pubkey),
                name = profile.name,
                bio = profile.about,
                pictureUrl = profile.picture,
                numOfFollowing = profile.numOfFollowing,
                numOfFollowers = profile.numOfFollowers,
                isOneself = profile.pubkey == pubkeyProvider.getPubkey(),
                isFollowed = Random.nextBoolean(),
                posts = posts.map { post ->
                    PostWithMeta(
                        name = profile.name,
                        id = UUID.randomUUID().toString(),
                        replyToId = UUID.randomUUID().toString(),
                        replyToName = UUID.randomUUID().toString(),
                        pubkey = profile.pubkey,
                        pictureUrl = profile.picture,
                        createdAt = post.createdAt,
                        content = post.content,
                        isLikedByMe = Random.nextBoolean(),
                        isRepostedByMe = Random.nextBoolean(),
                    )
                },
            )
        }
    }

    private suspend fun useCachedValues(pubkey: String) {
        val cachedProfile = profileDao.getProfile(pubkey)
        val cachedPosts = eventDao.listEventsFromPubkey(pubkey)
        if (cachedProfile != null) {
            Log.i(TAG, "Using cached values")
            viewModelState.update {
                it.copy(
                    pubkey = pubkey,
                    npub = hexToNpub(pubkey),
                    name = cachedProfile.name,
                    bio = cachedProfile.about,
                    pictureUrl = cachedProfile.picture,
                    numOfFollowing = cachedProfile.numOfFollowing,
                    numOfFollowers = cachedProfile.numOfFollowers,
                    isOneself = cachedProfile.pubkey == pubkeyProvider.getPubkey(),
                    isFollowed = Random.nextBoolean(),
                    posts = cachedPosts.map { post ->
                        PostWithMeta(
                            name = cachedProfile.name,
                            id = UUID.randomUUID().toString(),
                            replyToId = UUID.randomUUID().toString(),
                            replyToName = UUID.randomUUID().toString(),
                            pictureUrl = "",
                            pubkey = cachedProfile.pubkey,
                            createdAt = post.createdAt,
                            content = post.content,
                            isLikedByMe = Random.nextBoolean(),
                            isRepostedByMe = Random.nextBoolean(),
                        )
                    }
                )
            }
        } else {
            Log.i(TAG, "Resetting UI state because cache is empty")
            resetValues(pubkey)
        }
    }

    private fun resetValues(pubkey: String) {
        viewModelState.update {
            it.copy(
                pubkey = pubkey,
                npub = hexToNpub(pubkey),
                name = "",
                bio = "",
                pictureUrl = "",
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
            pubkeyProvider: IPubkeyProvider,
            profileDao: ProfileDao,
            eventDao: EventDao,
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
                        pubkeyProvider = pubkeyProvider,
                        profileDao = profileDao,
                        eventDao = eventDao,
                        context = context,
                        clip = clip,
                    ) as T
                }
            }
    }
}
