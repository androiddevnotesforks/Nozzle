package com.kaiwolfram.nozzle.ui.app.views.profile

import android.content.Context
import android.util.Log
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.kaiwolfram.nozzle.data.INostrRepository
import com.kaiwolfram.nozzle.model.Post
import com.kaiwolfram.nozzle.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "ProfileViewModel"

private val emptyPainter = object : Painter() {
    override val intrinsicSize: Size
        get() = Size.Zero

    override fun DrawScope.onDraw() {
        throw IllegalStateException("empty painter should be overwritten")
    }
}

data class ProfileViewModelState(
    val profilePicture: Painter = emptyPainter,
    val profilePictureUrl: String = "",
    val shortenedPubKey: String = "",
    val pubKey: String = "",
    val name: String = "",
    val bio: String = "",
    val posts: List<Post> = listOf(),
    val numOfFollowers: UInt = 0u,
    val numOfFollowing: UInt = 0u,
    val isRefreshing: Boolean = false,
    val isSyncing: Boolean = false,
    val followingList: List<Profile> = listOf(),
)

class ProfileViewModel(
    private val defaultProfilePicture: Painter,
    private val nostrRepository: INostrRepository,
    private val imageLoader: ImageLoader,
    context: Context
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ProfileViewModelState())
    private val pictures = mutableMapOf<String, Painter>()
    private val imageRequestBuilder = ImageRequest.Builder(context)

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
                profilePicture = defaultProfilePicture
            )
        }
        refreshProfileView()
    }

    val onRefreshProfileView: () -> Unit = {
        execWhenSyncingNotBlocked {
            Log.i(TAG, "Refresh profile view")
            setRefreshAndSync(true)
            refreshProfileView()
        }
    }

    val onRefreshFollowingList: () -> Unit = {
        execWhenSyncingNotBlocked {
            Log.i(TAG, "Refresh following list")
            setRefreshAndSync(true)
            viewModelScope.launch(context = Dispatchers.IO) {
                val following = nostrRepository.listFollowedProfiles(uiState.value.pubKey)
                viewModelState.update {
                    it.copy(
                        isRefreshing = false,
                        isSyncing = false,
                        followingList = following,
                    )
                }
                for (profile in following) {
                    viewModelScope.launch(context = Dispatchers.IO) {
                        val pic = requestPicture(url = profile.picture)
                        pictures[profile.picture] = pic
                    }
                }
            }
        }
    }

    val onGetPicture: (url: String) -> Painter = {
        pictures[it] ?: defaultProfilePicture
    }

    private fun execWhenSyncingNotBlocked(exec: () -> Unit) {
        if (uiState.value.isSyncing) {
            Log.i(TAG, "Blocked sync attempt")
        } else {
            exec()
        }
    }

    private fun setRefreshAndSync(value: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = value, isSyncing = value)
        }
    }

    private fun refreshProfileView() {
        val posts = nostrRepository.listPosts(uiState.value.pubKey)
        val profile = nostrRepository.getProfile(UUID.randomUUID().toString())
        val following = nostrRepository.listFollowedProfiles(profile.pubKey)
        viewModelState.update {
            it.copy(
                posts = posts,
                numOfFollowers = nostrRepository.getFollowerCount(),
                numOfFollowing = nostrRepository.getFollowingCount(),
                name = profile.name,
                bio = profile.bio,
                profilePictureUrl = profile.picture,
                shortenedPubKey = "${profile.pubKey.substring(0, 15)}...",
                pubKey = profile.pubKey,
                isRefreshing = false,
                isSyncing = false,
                followingList = following,
            )
        }
        updateProfilePicture(url = viewModelState.value.profilePictureUrl)
        for (post in posts) {
            viewModelScope.launch(context = Dispatchers.IO) {
                val pic = requestPicture(url = post.profilePicUrl)
                pictures[post.profilePicUrl] = pic
            }
        }
        for (followed in following) {
            viewModelScope.launch(context = Dispatchers.IO) {
                val pic = requestPicture(url = followed.picture)
                pictures[followed.picture] = pic
            }
        }
    }

    private fun updateProfilePicture(url: String) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val result = requestPicture(url = url)
            viewModelState.update {
                it.copy(profilePicture = result)
            }
        }
    }

    private suspend fun requestPicture(url: String): Painter {
        val request = imageRequestBuilder
            .data(url)
            .allowConversionToBitmap(true)
            .build()
        val result = imageLoader.execute(request).drawable?.toBitmap()?.asImageBitmap()
        return if (result != null) BitmapPainter(result) else defaultProfilePicture
    }

    companion object {
        fun provideFactory(
            defaultProfilePicture: Painter,
            nostrRepository: INostrRepository,
            imageLoader: ImageLoader,
            context: Context,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(
                        defaultProfilePicture = defaultProfilePicture,
                        nostrRepository = nostrRepository,
                        imageLoader = imageLoader,
                        context = context
                    ) as T
                }
            }
    }
}
