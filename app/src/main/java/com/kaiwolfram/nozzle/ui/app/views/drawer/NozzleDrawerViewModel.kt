package com.kaiwolfram.nozzle.ui.app.views.drawer


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.provider.IPersonalProfileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "NozzleDrawerViewModel"

data class NozzleDrawerViewModelState(
    val pubkey: String = "",
    val npub: String = "",
    val name: String = "",
    val pictureUrl: String = "",
)

class NozzleDrawerViewModel(
    private val personalProfileProvider: IPersonalProfileProvider,
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
        viewModelScope.launch(context = Dispatchers.IO) {
            useCachedValues()
        }
    }

    val onResetUiState: () -> Unit = {
        Log.i(TAG, "Reset UI")
        viewModelScope.launch(context = Dispatchers.IO) {
            useCachedValues()
        }
    }

    private suspend fun useCachedValues() {
        Log.i(TAG, "Set cached values")
        val meta = personalProfileProvider.getMetadata()
        viewModelState.update {
            it.copy(
                pubkey = personalProfileProvider.getPubkey(),
                npub = personalProfileProvider.getNpub(),
                name = meta?.name.orEmpty(),
                pictureUrl = meta?.picture.orEmpty(),
            )
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        fun provideFactory(personalProfileProvider: IPersonalProfileProvider): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NozzleDrawerViewModel(
                        personalProfileProvider = personalProfileProvider,
                    ) as T
                }
            }
    }
}
