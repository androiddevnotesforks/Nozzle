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
) {
}

class ProfileViewModel(
    private val defaultProfilePicture: Painter,
    nostrRepository: INostrRepository,
    imageLoader: ImageLoader,
    context: Context
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ProfileViewModelState())
    private val pictures = mutableMapOf<String, Painter>()

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize ProfileViewModel")
        val posts = nostrRepository.getPosts(uiState.value.pubKey)
        val profile = nostrRepository.getProfile(UUID.randomUUID().toString())
        viewModelState.update {
            it.copy(
                profilePicture = defaultProfilePicture,
                posts = posts,
                numOfFollowers = nostrRepository.getFollowerCount(),
                numOfFollowing = nostrRepository.getFollowingCount(),
                name = profile.name,
                bio = profile.bio,
                profilePictureUrl = profile.picture,
                shortenedPubKey = "${profile.pubKey.substring(0, 15)}...",
                pubKey = profile.pubKey
            )
        }
        for (post in posts) {
            viewModelScope.launch(context = Dispatchers.IO) {
                val pic = requestPicture(
                    url = post.profilePicUrl,
                    context = context,
                    imageLoader = imageLoader
                )
                pictures[post.profilePicUrl] = pic
            }
        }

        updateProfilePicture(
            url = viewModelState.value.profilePictureUrl,
            context = context,
            imageLoader = imageLoader
        )
    }

    val onGetPicture: (url: String) -> Painter = {
        pictures[it] ?:defaultProfilePicture
    }

    private fun updateProfilePicture(url: String, context: Context, imageLoader: ImageLoader) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val result = requestPicture(url = url, context = context, imageLoader = imageLoader)
            viewModelState.update {
                it.copy(profilePicture = result)
            }
        }
    }

    private suspend fun requestPicture(
        url: String,
        context: Context,
        imageLoader: ImageLoader
    ): Painter {
        val request = ImageRequest.Builder(context)
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
