package com.kaiwolfram.nozzle.ui.app.views.drawer


import android.util.Log
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.PictureRequester
import com.kaiwolfram.nozzle.data.nostr.INostrRepository
import com.kaiwolfram.nozzle.data.nostr.NostrProfile
import com.kaiwolfram.nozzle.data.preferences.ProfilePreferences
import com.kaiwolfram.nozzle.data.utils.createEmptyPainter
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
    val picture: Painter = createEmptyPainter(),
)

class NozzleDrawerViewModel(
    private val defaultProfilePicture: Painter,
    private val pictureRequester: PictureRequester,
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
        setCachedValues()
        updateValuesFromNostrMetaData()
    }

    private fun setCachedValues() {
        viewModelState.update {
            it.copy(
                pubkey = profilePreferences.getPubkey(),
                name = profilePreferences.getName(),
                pictureUrl = profilePreferences.getPictureUrl(),
                picture = defaultProfilePicture,
            )
        }
    }

    private fun updateValuesFromNostrMetaData() {
        viewModelScope.launch(context = Dispatchers.IO) {
            val profile = nostrRepository.getProfile(profilePreferences.getPubkey())
            if (profile != null) {
                cacheProfile(profile)
                fetchPictureAndUpdateUiState(profile)
            }
        }
    }

    private fun cacheProfile(profile: NostrProfile) {
        profilePreferences.setProfileValues(profile)
    }

    private suspend fun fetchPictureAndUpdateUiState(profile: NostrProfile) {
        viewModelState.update {
            it.copy(
                name = profile.name,
                pictureUrl = profile.picture,
                picture = pictureRequester.request(profile.picture) ?: defaultProfilePicture
            )
        }
    }

    companion object {
        fun provideFactory(
            defaultProfilePicture: Painter,
            pictureRequester: PictureRequester,
            nostrRepository: INostrRepository,
            profilePreferences: ProfilePreferences,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NozzleDrawerViewModel(
                        defaultProfilePicture = defaultProfilePicture,
                        pictureRequester = pictureRequester,
                        nostrRepository = nostrRepository,
                        profilePreferences = profilePreferences,
                    ) as T
                }
            }
    }
}
