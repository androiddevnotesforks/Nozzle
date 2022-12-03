package com.kaiwolfram.nozzle.ui.app.views.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

private const val TAG = "SettingsViewModel"

data class SettingsViewModelState(
    val name: String = "Settings are coming soon!",
    val bio: String = "Settings are coming soon!",
    val pictureUrl: String = "Settings are coming soon!",
)

class SettingsViewModel : ViewModel() {
    private val viewModelState = MutableStateFlow(SettingsViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )


    init {
        Log.i(TAG, "Initialize SettingsViewModel")
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel() as T
            }
        }
    }
}
