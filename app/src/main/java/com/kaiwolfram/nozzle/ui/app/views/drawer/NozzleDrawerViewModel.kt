package com.kaiwolfram.nozzle.ui.app.views.drawer


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.preferences.profile.IProfileProvider
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val TAG = "NozzleDrawerViewModel"

data class NozzleDrawerViewModelState(
    val pubkey: String = "",
    val npub: String = "",
    val name: String = "",
    val pictureUrl: String = "",
)

class NozzleDrawerViewModel(
    private val profileProvider: IProfileProvider,
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
    }

    val onResetUiState: () -> Unit = {
        useCachedValues()
    }

    private fun useCachedValues() {
        Log.i(TAG, "Set cached values")
        viewModelState.update {
            it.copy(
                pubkey = profileProvider.getPubkey(),
                npub = profileProvider.getNpub(),
                name = profileProvider.getName(),
                pictureUrl = profileProvider.getPictureUrl(),
            )
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        fun provideFactory(profileProvider: IProfileProvider): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NozzleDrawerViewModel(
                        profileProvider = profileProvider,
                    ) as T
                }
            }
    }
}
