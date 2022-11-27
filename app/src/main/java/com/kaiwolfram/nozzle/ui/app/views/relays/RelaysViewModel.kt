package com.kaiwolfram.nozzle.ui.app.views.relays

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

private const val TAG = "RelaysViewModel"

data class RelaysViewModelState(
    val label: String = "Relays are coming soon!",
)

class RelaysViewModel : ViewModel() {
    private val viewModelState = MutableStateFlow(RelaysViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )


    init {
        Log.i(TAG, "Initialize RelaysViewModel")
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RelaysViewModel() as T
            }
        }
    }
}
