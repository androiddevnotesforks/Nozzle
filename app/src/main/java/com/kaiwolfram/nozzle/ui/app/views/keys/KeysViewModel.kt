package com.kaiwolfram.nozzle.ui.app.views.keys

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

private const val TAG = "KeysViewModel"

data class KeysViewModelState(
    val label: String = "Keys are coming soon!",
)

class KeysViewModel : ViewModel() {
    private val viewModelState = MutableStateFlow(KeysViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )


    init {
        Log.i(TAG, "Initialize KeysViewModel")
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return KeysViewModel() as T
            }
        }
    }
}
