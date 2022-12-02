package com.kaiwolfram.nozzle.ui.app.views.keys

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.preferences.ProfilePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val TAG = "KeysViewModel"

data class KeysViewModelState(
    val pubkey: String = "",
    val privkey: String = "",
)

class KeysViewModel(
    profilePreferences: ProfilePreferences,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(KeysViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )


    init {
        Log.i(TAG, "Initialize KeysViewModel")
        viewModelState.update {
            it.copy(
                privkey = profilePreferences.getPrivkey(),
                pubkey = profilePreferences.getPubkey(),
            )
        }
    }

    companion object {
        fun provideFactory(profilePreferences: ProfilePreferences): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return KeysViewModel(profilePreferences = profilePreferences) as T
                }
            }
    }
}
