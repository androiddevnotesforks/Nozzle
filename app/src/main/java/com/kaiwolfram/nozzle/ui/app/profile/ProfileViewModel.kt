package com.kaiwolfram.nozzle.ui.app.profile

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
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

data class ProfileViewModelState(
    val profilePicture: Drawable? = null,
    val profilePictureUrl: String = "https://avatars.githubusercontent.com/u/48265657?v=4",
    val shortenedPubKey: String = "c1a8cf31...9328574a",
    val privateKey: String = "12341234123412341234123412341234",
    val name: String = "Kai Wolfram",
    val bio: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit.",
)

class ProfileViewModel(imageLoader: ImageLoader, context: Context) : ViewModel() {
    private val viewModelState = MutableStateFlow(ProfileViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize ProfileViewModel")
        updateProfilePicture(
            url = viewModelState.value.profilePictureUrl,
            context = context,
            imageLoader = imageLoader
        )
    }

    val onChangeProfilePictureUrl: (String) -> Unit = { newUrl: String ->
        if (newUrl != uiState.value.profilePictureUrl) {
            viewModelState.update {
                it.copy(profilePictureUrl = newUrl)
            }
            Log.i(TAG, "Changed URL to $newUrl")
            updateProfilePicture(
                url = newUrl,
                context = context,
                imageLoader = imageLoader
            )
        }
    }

    val onChangeName: (String) -> Unit = { newName: String ->
        if (newName != uiState.value.name) {
            viewModelState.update {
                it.copy(name = newName)
            }
            Log.i(TAG, "Changed name to $newName")
        }
    }

    val onChangeBio: (String) -> Unit = { newBio: String ->
        if (newBio != uiState.value.bio) {
            viewModelState.update {
                it.copy(bio = newBio)
            }
            Log.i(TAG, "Changed bio to $newBio")
        }
    }

    val onChangePrivateKey: (String) -> Unit = { newPrivateKey: String ->
        if (newPrivateKey != uiState.value.privateKey) {
            viewModelState.update {
                it.copy(privateKey = newPrivateKey)
            }
            Log.i(TAG, "Changed private key to $newPrivateKey")
        }
    }

    private fun updateProfilePicture(url: String, context: Context, imageLoader: ImageLoader) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()
            val drawable = imageLoader.execute(request).drawable
            val msg = if (drawable == null)
                "Failed to load profile picture"
            else
                "Successfully fetched profile picture"
            Log.i(TAG, msg)
            viewModelState.update {
                it.copy(profilePicture = drawable)
            }
        }
    }

    companion object {
        fun provideFactory(imageLoader: ImageLoader, context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(imageLoader = imageLoader, context = context) as T
                }
            }
    }
}
