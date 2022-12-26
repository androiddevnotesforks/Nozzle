package com.kaiwolfram.nozzle.ui.app.views.editProfile

import android.content.Context
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.isValidUsername
import com.kaiwolfram.nozzle.data.preferences.profile.IProfileCache
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "EditProfileViewModel"

data class EditProfileViewModelState(
    val usernameInput: String = "",
    val bioInput: String = "",
    val pictureUrlInput: String = "",
    val nip05Input: String = "",
    val hasChanges: Boolean = false,
    val isInvalidUsername: Boolean = false,
    val isInvalidPictureUrl: Boolean = false,
)

class EditProfileViewModel(
    private val profileCache: IProfileCache,
    private val profileDao: ProfileDao,
    context: Context,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(EditProfileViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize EditProfileViewModel")
        useCachedValues()
    }

    val onUpdateProfileAndShowToast: (String) -> Unit = { toast ->
        uiState.value.let {
            if (!it.hasChanges) {
                Log.i(TAG, "Profile editor has no changes")
                return@let
            }
            val isValidUsername = isValidUsername(it.usernameInput)
            val isValidUrl = isValidUrl(it.pictureUrlInput)
            if (isValidUsername && isValidUrl) {
                Log.i(TAG, "Updating profile")
                viewModelScope.launch(context = Dispatchers.IO) {
                    profileDao.updateMetaData(
                        pubkey = profileCache.getPubkey(),
                        name = it.usernameInput,
                        bio = it.bioInput,
                        pictureUrl = it.pictureUrlInput
                    )
                }
                profileCache.setName(it.usernameInput)
                profileCache.setBio(it.bioInput)
                profileCache.setPictureUrl(it.pictureUrlInput)
                profileCache.setNip05(it.nip05Input)
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
        uiState.value.let { state ->
            viewModelState.update {
                it.copy(usernameInput = input)
            }
            setHasChanges()
            if (state.isInvalidUsername && isValidUsername(input)) {
                viewModelState.update {
                    it.copy(isInvalidUsername = false)
                }
            }
        }
    }

    val onChangeBio: (String) -> Unit = { input ->
        if (input != uiState.value.bioInput) {
            viewModelState.update {
                it.copy(bioInput = input)
            }
            setHasChanges()
        }
    }

    val onChangePictureUrl: (String) -> Unit = { input ->
        uiState.value.let { state ->
            viewModelState.update {
                it.copy(pictureUrlInput = input)
            }
            setHasChanges()
            if (state.isInvalidPictureUrl && isValidUrl(input)) {
                viewModelState.update {
                    it.copy(isInvalidPictureUrl = false)
                }
            }
        }
    }

    val onChangeNip05: (String) -> Unit = { input ->
        viewModelState.update {
            it.copy(nip05Input = input)
        }
        setHasChanges()
    }

    val onCanGoBack: () -> Boolean = {
        val canGoBack = uiState.value.let { state ->
            !state.isInvalidUsername && !state.isInvalidPictureUrl
        }
        Log.i(TAG, "can go back $canGoBack")
        canGoBack
    }

    val onResetUiState: () -> Unit = {
        useCachedValues()
    }

    private fun isValidUrl(url: String) = url.isEmpty() || URLUtil.isValidUrl(url)


    private fun setHasChanges() {
        uiState.value.let {
            val hasChanges = it.usernameInput != profileCache.getName()
                    || it.bioInput != profileCache.getBio()
                    || it.pictureUrlInput != profileCache.getPictureUrl()
                    || it.nip05Input != profileCache.getNip05()
            if (hasChanges != it.hasChanges) {
                viewModelState.update { state ->
                    state.copy(hasChanges = hasChanges)
                }
            }
        }
    }

    private fun useCachedValues() {
        viewModelState.update {
            it.copy(
                usernameInput = profileCache.getName(),
                bioInput = profileCache.getBio(),
                pictureUrlInput = profileCache.getPictureUrl(),
                nip05Input = profileCache.getNip05(),
                hasChanges = false,
                isInvalidUsername = false,
                isInvalidPictureUrl = false
            )
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        fun provideFactory(
            profileCache: IProfileCache,
            profileDao: ProfileDao,
            context: Context,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EditProfileViewModel(
                    profileCache = profileCache,
                    profileDao = profileDao,
                    context = context
                ) as T
            }
        }
    }
}
