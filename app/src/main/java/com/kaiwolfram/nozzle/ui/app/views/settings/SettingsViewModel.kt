package com.kaiwolfram.nozzle.ui.app.views.settings

import android.content.Context
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.isValidUsername
import com.kaiwolfram.nozzle.data.preferences.ProfilePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val TAG = "SettingsViewModel"

data class SettingsViewModelState(
    val usernameInput: String = "",
    val bioInput: String = "",
    val pictureUrlInput: String = "",
    val hasChanges: Boolean = false,
    val isInvalidUsername: Boolean = false,
    val isInvalidPictureUrl: Boolean = false,
)

class SettingsViewModel(
    private val profilePreferences: ProfilePreferences,
    context: Context,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(SettingsViewModelState())
    private var username: String = ""
    private var bio: String = ""
    private var pictureUrl: String = ""

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize SettingsViewModel")
        useCachedValues()
    }

    val onUpdateProfileAndShowToast: (String) -> Unit =
        { toast ->
            uiState.value.let {
                val isValidUsername = isValidUsername(it.usernameInput)
                val isValidUrl = it.pictureUrlInput.isEmpty()
                        || URLUtil.isValidUrl(it.pictureUrlInput)
                if (isValidUsername && isValidUrl) {
                    Log.i(TAG, "Updating profile")
                    profilePreferences.setName(it.usernameInput)
                    profilePreferences.setBio(it.bioInput)
                    profilePreferences.setPictureUrl(it.pictureUrlInput)
                    useCachedValues()
                    Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
                } else {
                    Log.i(TAG, "New values are invalid")
                    viewModelState.update { state ->
                        state.copy(
                            isInvalidUsername = !isValidUsername,
                            isInvalidPictureUrl = !isValidUrl
                        )
                    }
                }
            }
        }

    val onChangeName: (String) -> Unit = { input ->
        viewModelState.update {
            it.copy(
                usernameInput = input,
                hasChanges = true
            )
        }
    }

    val onChangeBio: (String) -> Unit = { input ->
        if (input != uiState.value.bioInput) {
            viewModelState.update {
                it.copy(
                    bioInput = input,
                    hasChanges = true
                )
            }
        }
    }

    val onChangePictureUrl: (String) -> Unit = { input ->
        viewModelState.update {
            it.copy(
                pictureUrlInput = input,
                hasChanges = true
            )
        }
    }

    val onResetUiState: () -> Unit = {
        useCachedValues()
    }

    private fun useCachedValues() {
        username = profilePreferences.getName()
        bio = profilePreferences.getBio()
        pictureUrl = profilePreferences.getPictureUrl()
        viewModelState.update {
            it.copy(
                usernameInput = username,
                bioInput = bio,
                pictureUrlInput = pictureUrl,
                hasChanges = false,
                isInvalidUsername = false,
                isInvalidPictureUrl = false
            )
        }
    }

    companion object {
        fun provideFactory(
            profilePreferences: ProfilePreferences,
            context: Context,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(
                    profilePreferences = profilePreferences,
                    context = context
                ) as T
            }
        }
    }
}
