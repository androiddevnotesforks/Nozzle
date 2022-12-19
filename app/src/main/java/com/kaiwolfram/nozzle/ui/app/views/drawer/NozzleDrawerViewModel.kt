package com.kaiwolfram.nozzle.ui.app.views.drawer


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrRepository
import com.kaiwolfram.nozzle.data.nostr.NostrProfile
import com.kaiwolfram.nozzle.data.preferences.ProfilePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "NozzleDrawerViewModel"

data class NozzleDrawerViewModelState(
    val pubkey: String = "",
    val name: String = "",
    val pictureUrl: String = "",
)

class NozzleDrawerViewModel(
    private val nostrRepository: INostrRepository,
    private val profilePreferences: ProfilePreferences,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(NozzleDrawerViewModelState())
    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize NozzleDrawerViewModel")
        useCachedValues()
        updateValuesFromNostrMetaData()
    }

    val onUpdatePubkey: () -> Unit = {
        val oldPubkey = uiState.value.pubkey
        val newPubkey = profilePreferences.getPubkey()
        if (newPubkey != oldPubkey) {
            Log.i(TAG, "Update pubkey from $oldPubkey to $newPubkey")
            viewModelState.update {
                it.copy(
                    pubkey = newPubkey,
                )
            }
        } else {
            Log.i(TAG, "Not updating pubkey: Keys are identical")
        }
    }

    val onUpdateName: () -> Unit = {
        val oldName = uiState.value.name
        val newName = profilePreferences.getName()
        if (newName != oldName) {
            Log.i(TAG, "Update name from $oldName to $newName")
            viewModelState.update {
                it.copy(
                    name = newName,
                )
            }
        } else {
            Log.i(TAG, "Not updating name: Names are identical")
        }
    }

    private fun useCachedValues() {
        viewModelState.update {
            it.copy(
                pubkey = profilePreferences.getPubkey(),
                name = profilePreferences.getName(),
                pictureUrl = profilePreferences.getPictureUrl(),
            )
        }
    }

    private fun updateValuesFromNostrMetaData() {
        viewModelScope.launch(context = Dispatchers.IO) {
            val profile = nostrRepository.getProfile(profilePreferences.getPubkey())
            if (profile != null) {
                cacheProfile(profile)
            }
        }
    }

    private fun cacheProfile(profile: NostrProfile) {
        profilePreferences.setProfileValues(profile)
    }

    companion object {
        fun provideFactory(
            nostrRepository: INostrRepository,
            profilePreferences: ProfilePreferences,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NozzleDrawerViewModel(
                        nostrRepository = nostrRepository,
                        profilePreferences = profilePreferences,
                    ) as T
                }
            }
    }
}
