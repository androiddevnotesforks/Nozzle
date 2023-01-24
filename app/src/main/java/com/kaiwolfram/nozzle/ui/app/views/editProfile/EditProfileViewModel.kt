package com.kaiwolfram.nozzle.ui.app.views.editProfile

import android.content.Context
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nostrclientkt.utils.NostrUtils.isValidUsername
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.data.manager.IPersonalProfileManager
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "EditProfileViewModel"

data class EditProfileViewModelState(
    val nameInput: String = "",
    val aboutInput: String = "",
    val pictureInput: String = "",
    val nip05Input: String = "",
    val hasChanges: Boolean = false,
    val isInvalidUsername: Boolean = false,
    val isInvalidPictureUrl: Boolean = false,
)

class EditProfileViewModel(
    private val personalProfileManager: IPersonalProfileManager,
    private val nostrSubscriber: INostrSubscriber,
    private val nostrService: INostrService,
    context: Context,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(EditProfileViewModelState())

    private var metadataState: Metadata? = null

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize EditProfileViewModel")
        nostrSubscriber.subscribeToProfileMetadataAndContactList(personalProfileManager.getPubkey())
        useCachedValues()
    }

    val onUpdateProfile: () -> Unit = {
        uiState.value.let {
            if (!it.hasChanges) {
                Log.i(TAG, "Profile editor has no changes")
                return@let
            }
            val isValidUsername = isValidUsername(it.nameInput)
            val isValidUrl = isValidUrl(it.pictureInput)
            if (isValidUsername && isValidUrl) {
                Log.i(TAG, "New values are valid. Update profile")
                viewModelScope.launch(context = Dispatchers.IO) {
                    updateMetadataInDb(it)
                    updateMetadataOverNostr(it)
                    useCachedValues()
                }
                Toast.makeText(
                    context,
                    context.getString(R.string.profile_updated),
                    Toast.LENGTH_SHORT
                ).show()
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
                it.copy(nameInput = input)
            }
            setHasChanges()
            if (state.isInvalidUsername && isValidUsername(input)) {
                viewModelState.update {
                    it.copy(isInvalidUsername = false)
                }
            }
        }
    }

    val onChangeAbout: (String) -> Unit = { input ->
        if (input != uiState.value.aboutInput) {
            viewModelState.update {
                it.copy(aboutInput = input)
            }
            setHasChanges()
        }
    }

    val onChangePicture: (String) -> Unit = { input ->
        uiState.value.let { state ->
            viewModelState.update {
                it.copy(pictureInput = input)
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
        canGoBack
    }

    val onResetUiState: () -> Unit = {
        Log.i(TAG, "Reset UI")
        useCachedValues()
    }

    private suspend fun updateMetadataInDb(state: EditProfileViewModelState) {
        Log.i(TAG, "Update profile in DB")
        personalProfileManager.setMeta(
            name = state.nameInput,
            about = state.aboutInput,
            picture = state.pictureInput,
            nip05 = state.nip05Input,
        )
    }

    private fun updateMetadataOverNostr(state: EditProfileViewModelState) {
        Log.i(TAG, "Update profile over nostr")
        val metadata = Metadata(
            name = state.nameInput,
            about = state.aboutInput,
            picture = state.pictureInput,
            nip05 = state.nip05Input
        )
        nostrService.publishProfile(metadata = metadata)
    }

    private fun isValidUrl(url: String) = url.isEmpty() || URLUtil.isValidUrl(url)

    private fun setHasChanges() {
        metadataState.let { metadata ->
            uiState.value.let {
                val hasChanges = it.nameInput != metadata?.name.orEmpty()
                        || it.aboutInput != metadata?.about.orEmpty()
                        || it.pictureInput != metadata?.picture.orEmpty()
                        || it.nip05Input != metadata?.nip05.orEmpty()
                if (hasChanges != it.hasChanges) {
                    viewModelState.update { state ->
                        state.copy(hasChanges = hasChanges)
                    }
                }
            }
        }
    }

    private fun useCachedValues() {
        Log.i(TAG, "Use cached values")
        collectLatestMetadata().invokeOnCompletion {
            metadataState.let { metadata ->
                viewModelState.update {
                    it.copy(
                        nameInput = metadata?.name.orEmpty(),
                        aboutInput = metadata?.about.orEmpty(),
                        pictureInput = metadata?.picture.orEmpty(),
                        nip05Input = metadata?.nip05.orEmpty(),
                        hasChanges = false,
                        isInvalidUsername = false,
                        isInvalidPictureUrl = false
                    )
                }
            }
        }
    }

    private fun collectLatestMetadata(): Job {
        Log.i(TAG, "Collect latest metadata")
        return viewModelScope.launch(Dispatchers.IO) {
            metadataState = personalProfileManager.getMetadata().firstOrNull()
        }
    }


    companion object {
        fun provideFactory(
            personalProfileManager: IPersonalProfileManager,
            nostrSubscriber: INostrSubscriber,
            nostrService: INostrService,
            context: Context,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EditProfileViewModel(
                    personalProfileManager = personalProfileManager,
                    nostrSubscriber = nostrSubscriber,
                    nostrService = nostrService,
                    context = context
                ) as T
            }
        }
    }
}
