package com.kaiwolfram.nozzle.ui.app.views.keys

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
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
    val hasChanges: Boolean = true,
) {
}

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

    val onCopyPubkeyAndShowToast: (Context, ClipboardManager, String) -> Unit =
        { context, clipboardManager, toast ->
            val pubkey = uiState.value.pubkey
            Log.i(TAG, "Copy pubkey $pubkey and show toast '$toast'")
            clipboardManager.setText(AnnotatedString(pubkey))
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
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
