package com.kaiwolfram.nozzle.ui.app.views.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.ellipsatePubkey
import com.kaiwolfram.nozzle.data.nostr.INostrRepository
import com.kaiwolfram.nozzle.data.room.dao.EventDao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.room.entity.EventEntity
import com.kaiwolfram.nozzle.data.room.entity.ProfileEntity
import com.kaiwolfram.nozzle.model.PostWithMeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "ProfileViewModel"

data class ProfileViewModelState(
    val shortenedPubkey: String = "",
    val pubkey: String = "",
    val name: String = "",
    val bio: String = "",
    val pictureUrl: String = "",
    val numOfFollowing: Int = 0,
    val numOfFollowers: Int = 0,
    val posts: List<PostWithMeta> = listOf(),
    val isRefreshing: Boolean = false,
)

class ProfileViewModel(
    private val nostrRepository: INostrRepository,
    context: Context,
    clip: ClipboardManager,
    private val profileDao: ProfileDao,
    private val eventDao: EventDao,
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

    val onCopyPubkeyAndShowToast: (String) -> Unit = { toast ->
        Log.i(TAG, "Copy pubkey ${uiState.value.pubkey} and show toast '$toast'")
        clip.setText(AnnotatedString(uiState.value.pubkey))
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
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

    private suspend fun fetchAndUseNostrData(pubkey: String) {
        Log.i(TAG, "Fetching nostr data for $pubkey")
        isSyncing.set(true)
        val nostrProfile = nostrRepository.getProfile(pubkey)
        if (nostrProfile != null) {
            val numOfFollowing = nostrRepository.getFollowingCount(pubkey)
            val numOfFollowers = nostrRepository.getFollowerCount(pubkey)
            val profile = ProfileEntity(
                pubkey = pubkey,
                name = nostrProfile.name,
                bio = nostrProfile.about,
                pictureUrl = nostrProfile.picture,
                numOfFollowing = numOfFollowing,
                numOfFollowers = numOfFollowers,
            )
            val posts = nostrRepository.listPosts(pubkey)
            useAndCacheProfile(profile = profile, posts = posts)
        }
        isSyncing.set(false)
        setRefresh(false)
    }

    private suspend fun useAndCacheProfile(
        profile: ProfileEntity,
        posts: List<EventEntity>,
    ) {
        Log.i(TAG, "Caching fetched profile of ${profile.pubkey}")
        profileDao.insert(profile)
        eventDao.insert(posts)
        viewModelState.update {
            it.copy(
                shortenedPubkey = ellipsatePubkey(profile.pubkey),
                pubkey = profile.pubkey,
                name = profile.name,
                bio = profile.bio,
                pictureUrl = profile.pictureUrl,
                numOfFollowing = profile.numOfFollowing,
                numOfFollowers = profile.numOfFollowers,
                posts = posts.map { post ->
                    PostWithMeta(
                        name = profile.name,
                        id = UUID.randomUUID().toString(),
                        replyToId = UUID.randomUUID().toString(),
                        replyToName = UUID.randomUUID().toString(),
                        pubkey = profile.pubkey,
                        pictureUrl = profile.pictureUrl,
                        createdAt = post.createdAt,
                        content = post.content
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
                    shortenedPubkey = ellipsatePubkey(pubkey),
                    pubkey = pubkey,
                    name = cachedProfile.name,
                    bio = cachedProfile.bio,
                    pictureUrl = cachedProfile.pictureUrl,
                    numOfFollowing = cachedProfile.numOfFollowing,
                    numOfFollowers = cachedProfile.numOfFollowers,
                    posts = cachedPosts.map { post ->
                        PostWithMeta(
                            name = cachedProfile.name,
                            id = UUID.randomUUID().toString(),
                            replyToId = UUID.randomUUID().toString(),
                            replyToName = UUID.randomUUID().toString(),
                            pictureUrl = "",
                            pubkey = cachedProfile.pubkey,
                            createdAt = post.createdAt,
                            content = post.content
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
                shortenedPubkey = ellipsatePubkey(pubkey),
                pubkey = pubkey,
                name = "",
                bio = "",
                pictureUrl = "",
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

    companion object {
        fun provideFactory(
            nostrRepository: INostrRepository,
            context: Context,
            clip: ClipboardManager,
            profileDao: ProfileDao,
            eventDao: EventDao,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(
                        nostrRepository = nostrRepository,
                        context = context,
                        clip = clip,
                        profileDao = profileDao,
                        eventDao = eventDao,
                    ) as T
                }
            }
    }
}
