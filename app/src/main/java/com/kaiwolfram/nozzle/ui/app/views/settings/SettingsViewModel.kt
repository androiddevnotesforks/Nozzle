package com.kaiwolfram.nozzle.ui.app.views.settings

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.utils.isHex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val TAG = "SettingsViewModel"

data class SettingsViewModelState(
    val username: String = "",
    val bio: String = "",
    val pictureUrl: String = "",
    val usernameIsInvalid: Boolean = true,
    val pictureUrlIsInvalid: Boolean = true,
    val hasChanges: Boolean = false,
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

    val onUpdateProfileAndShowToast: (String) -> Unit =
        { toast ->
            TODO(toast)
        }

    val onResetUiState: () -> Unit = {
        viewModelState.update {
            it.copy(
                hasChanges = false,
                // TODO: other fields
            )
        }
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
