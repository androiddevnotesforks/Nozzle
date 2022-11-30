package com.kaiwolfram.nozzle.ui.app.views.profile

import android.util.Log
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.INostrRepository
import com.kaiwolfram.nozzle.data.PictureRequester
import com.kaiwolfram.nozzle.data.utils.createEmptyPainter
import com.kaiwolfram.nozzle.model.NozzleProfile
import com.kaiwolfram.nozzle.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ProfileViewModel"

data class ProfileViewModelState(
    val publicKey: String = "",
    val name: String = "",
    val bio: String = "",
    val pictureUrl: String = "",
    val picture: Painter = createEmptyPainter(),
    val numOfFollowing: UInt = 0u,
    val numOfFollowers: UInt = 0u,
    val posts: List<Post> = listOf(),
    val isRefreshing: Boolean = false,
    val isSyncing: Boolean = false,
)

class ProfileViewModel(
    private val defaultProfilePicture: Painter,
    private val nostrRepository: INostrRepository,
    private val pictureRequester: PictureRequester,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ProfileViewModelState())

    private val profiles = mutableMapOf<String, NozzleProfile>()
    private val pictures = mutableMapOf<String, Painter>()

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize ProfileViewModel")
        viewModelState.update {
            it.copy(
                picture = defaultProfilePicture
            )
        }
    }

    val onSetPublicKey: (String) -> Unit = { publicKey ->
        Log.i(TAG, "Setting ui data for $publicKey")
        useCachedValues(publicKey)
        fetchAndUseNostrData(publicKey)
    }

    val onGetPicture: (String) -> Painter = { pictureUrl ->
        val picture = pictures[pictureUrl]
        if (picture == null) {
            viewModelScope.launch(context = Dispatchers.IO) {
                requestAndCachePicture(pictureUrl)
            }
        }
        picture ?: defaultProfilePicture
    }

    val onRefreshProfileView: () -> Unit = {
        execWhenSyncingNotBlocked {
            Log.i(TAG, "Refresh profile view")
            setRefresh(true)
            fetchAndUseNostrData(uiState.value.publicKey)
        }
    }

    private suspend fun requestAndCachePicture(pictureUrl: String): Painter? {
        val picture = pictureRequester.request(pictureUrl)
        if (picture != null) {
            pictures[pictureUrl] = picture
        }
        return picture
    }

    private fun fetchAndUseNostrData(publicKey: String) {
        Log.i(TAG, "Fetching nostr data for $publicKey")
        setSync(true)
        viewModelScope.launch(context = Dispatchers.IO) {
            val profile = nostrRepository.getProfile(publicKey)
            if (profile != null) {
                val picture = pictures[publicKey]
                    ?: pictureRequester.request(profile.pictureUrl)
                    ?: defaultProfilePicture
                val numOfFollowing = nostrRepository.getFollowingCount(publicKey)
                val numOfFollowers = nostrRepository.getFollowerCount(publicKey)
                val posts = nostrRepository.listPosts(publicKey)
                val nozzleProfile =
                    NozzleProfile(profile, picture, numOfFollowing, numOfFollowers, posts)
                useAndCacheProfile(nozzleProfile)
            }
            setSync(false)
            setRefresh(false)
        }
    }

    private fun useAndCacheProfile(profile: NozzleProfile) {
        Log.i(TAG, "Caching fetched profile of ${profile.profile.publicKey}")
        profiles[profile.profile.publicKey] = profile
        pictures[profile.profile.pictureUrl] = profile.picture
        viewModelState.update {
            it.copy(
                publicKey = profile.profile.publicKey,
                name = profile.profile.name,
                bio = profile.profile.bio,
                pictureUrl = profile.profile.pictureUrl,
                picture = profile.picture,
                numOfFollowing = profile.numOfFollowing,
                numOfFollowers = profile.numOfFollowers,
                posts = profile.posts,
            )
        }
    }

    private fun useCachedValues(publicKey: String) {
        val profile = profiles[publicKey]
        if (profile != null) {
            Log.i(TAG, "Using cached values")
            viewModelState.update {
                it.copy(
                    publicKey = publicKey,
                    name = profile.profile.name,
                    bio = profile.profile.bio,
                    pictureUrl = profile.profile.pictureUrl,
                    picture = profile.picture,
                    numOfFollowing = profile.numOfFollowing,
                    numOfFollowers = profile.numOfFollowers,
                    posts = profile.posts,
                )
            }
        } else {
            Log.i(TAG, "Resetting ui state because cache is empty")
            resetValues(publicKey)
        }
    }

    private fun resetValues(publicKey: String) {
        viewModelState.update {
            it.copy(
                publicKey = publicKey,
                name = "",
                bio = "",
                pictureUrl = "",
                picture = defaultProfilePicture,
                numOfFollowing = 0u,
                numOfFollowers = 0u,
                posts = listOf(),
            )
        }
    }

    private fun execWhenSyncingNotBlocked(exec: () -> Unit) {
        if (uiState.value.isSyncing) {
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

    private fun setSync(value: Boolean) {
        viewModelState.update {
            it.copy(isSyncing = value)
        }
    }

    companion object {
        fun provideFactory(
            defaultProfilePicture: Painter,
            nostrRepository: INostrRepository,
            pictureRequester: PictureRequester,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(
                        defaultProfilePicture = defaultProfilePicture,
                        nostrRepository = nostrRepository,
                        pictureRequester = pictureRequester,
                    ) as T
                }
            }
    }
}
