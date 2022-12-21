package com.kaiwolfram.nozzle.ui.app.views.drawer


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.preferences.IPersonalProfileStorageReader
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val TAG = "NozzleDrawerViewModel"

data class NozzleDrawerViewModelState(
    val pubkey: String = "",
    val name: String = "",
    val pictureUrl: String = "",
)

class NozzleDrawerViewModel(
    private val profileStorageReader: IPersonalProfileStorageReader,
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
        viewModelState.update {
            it.copy(
                pubkey = profileStorageReader.getPubkey(),
                name = profileStorageReader.getName(),
                pictureUrl = profileStorageReader.getPictureUrl(),
            )
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        fun provideFactory(profileStorageReader: IPersonalProfileStorageReader): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NozzleDrawerViewModel(
                        profileStorageReader = profileStorageReader,
                    ) as T
                }
            }
    }
}
