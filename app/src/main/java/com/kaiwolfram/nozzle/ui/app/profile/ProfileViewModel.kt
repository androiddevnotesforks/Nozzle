package com.kaiwolfram.nozzle.ui.app.profile

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val TAG = "ProfileViewModel"

data class ProfileViewModelState(
    val profilePicture: ImageVector = Icons.Rounded.Person,
    val profilePictureUrl: String = "https://robohash.org/kai",
    val shortenedPubKey: String = "12345...abcde",
    val privateKey: String = "12341234123412341234123412341234",
    val name: String = "Kai Wolfram",
    val bio: String = "Hola soy Kai y aqui hay informatciones sobre mi. Bla bla bla",
)

class ProfileViewModel : ViewModel() {
    private val viewModelState = MutableStateFlow(ProfileViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize ProfileViewModel")
    }

    val onChangeProfilePictureUrl: (String) -> Unit = { newUrl: String ->
        if (newUrl != uiState.value.profilePictureUrl) {
            viewModelState.update {
                it.copy(profilePictureUrl = newUrl)
            }
            Log.i(TAG, "Changed URL to $newUrl")
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

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel() as T
            }
        }
    }
}
