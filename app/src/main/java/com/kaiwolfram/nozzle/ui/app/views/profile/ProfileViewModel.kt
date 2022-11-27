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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val profilePictureUrl: String = "https://avatars.githubusercontent.com/u/48265657?v=4",
    val shortenedPubKey: String = "c1a8cf31...9328574a",
    val privateKey: String = "12341234123412341234123412341234",
    val name: String = "Kai Wolfram",
    val bio: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit.",
)

class ProfileViewModel(
    private val defaultProfilePicture: Painter,
    imageLoader: ImageLoader,
    context: Context
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ProfileViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize ProfileViewModel")
        viewModelState.update {
            it.copy(profilePicture = defaultProfilePicture)
        }
        updateProfilePicture(
            url = viewModelState.value.profilePictureUrl,
            context = context,
            imageLoader = imageLoader
        )
    }

    val onChangeProfilePictureUrl: (String) -> Unit = {
        val newUrl = it.trim()
        if (newUrl != uiState.value.profilePictureUrl) {
            Log.i(TAG, "Change URL to $newUrl")
            viewModelState.update { state ->
                state.copy(profilePictureUrl = newUrl)
            }
            updateProfilePicture(
                url = newUrl,
                context = context,
                imageLoader = imageLoader
            )
        }
    }

    val onChangeName: (String) -> Unit = {
        val newName = it.trim()
        if (newName != uiState.value.name) {
            Log.i(TAG, "Change name to $newName")
            viewModelState.update { state ->
                state.copy(name = newName)
            }
        }
    }

    val onChangeBio: (String) -> Unit = {
        val newBio = it.trim()
        if (newBio != uiState.value.bio) {
            Log.i(TAG, "Change bio to $newBio")
            viewModelState.update { state ->
                state.copy(bio = newBio)
            }
        }
    }

    val onChangePrivateKey: (String) -> Unit = {
        val newPrivateKey = it.trim()
        if (newPrivateKey != uiState.value.privateKey) {
            Log.i(TAG, "Change private key to $newPrivateKey")
            viewModelState.update { state ->
                state.copy(privateKey = newPrivateKey)
            }
        }
    }

    private fun updateProfilePicture(url: String, context: Context, imageLoader: ImageLoader) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowConversionToBitmap(true)
                .build()
            imageLoader.execute(request).drawable?.let { fetched ->
                Log.i(TAG, "Successfully fetched image and updated profile picture")
                viewModelState.update {
                    it.copy(profilePicture = BitmapPainter(fetched.toBitmap().asImageBitmap()))
                }
            } ?: run {
                Log.i(TAG, "Failed to fetch image. Setting default as profile picture")
                viewModelState.update {
                    it.copy(profilePicture = defaultProfilePicture)
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            defaultProfilePicture: Painter,
            imageLoader: ImageLoader,
            context: Context,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(
                        defaultProfilePicture = defaultProfilePicture,
                        imageLoader = imageLoader,
                        context = context
                    ) as T
                }
            }
    }
}
