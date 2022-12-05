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
import com.kaiwolfram.nozzle.data.utils.isHex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val TAG = "KeysViewModel"

data class KeysViewModelState(
    val pubkey: String = "",
    val privkey: String = "",
    val newPrivkey: String = "",
    val hasChanges: Boolean = false,
    val isInvalid: Boolean = false,
)

class KeysViewModel(
    private val profilePreferences: ProfilePreferences,
    context: Context,
    clip: ClipboardManager,
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
        setFromPreferences()
    }

    private fun setFromPreferences() {
        viewModelState.update {
            val privKey = profilePreferences.getPrivkey()
            it.copy(
                privkey = privKey,
                newPrivkey = privKey,
                pubkey = profilePreferences.getPubkey(),
                hasChanges = false,
                isInvalid = false,
            )
        }
    }

    val onCopyPubkeyAndShowToast: (String) -> Unit = { toast ->
        val pubkey = uiState.value.pubkey
        Log.i(TAG, "Copy pubkey $pubkey and show toast '$toast'")
        clip.setText(AnnotatedString(pubkey))
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
    }

    val onUpdateKeyPairAndShowToast: (String) -> Unit =
        { toast ->
            val newPrivkey = uiState.value.newPrivkey
            val isValid = newPrivkey.length == 64 && newPrivkey.isHex()
            if (isValid) {
                Log.i(TAG, "Saving new privkey $newPrivkey")
                profilePreferences.setPrivkey(newPrivkey)
                setFromPreferences()
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "New privkey $newPrivkey is invalid")
                viewModelState.update {
                    it.copy(isInvalid = true)
                }
            }
        }

    val onPrivkeyChange: (String) -> Unit = { newValue ->
        if (uiState.value.privkey == newValue) {
            viewModelState.update {
                it.copy(
                    newPrivkey = newValue,
                    isInvalid = false,
                    hasChanges = false,
                )
            }
        } else if (uiState.value.newPrivkey != newValue) {
            viewModelState.update {
                it.copy(
                    newPrivkey = newValue,
                    isInvalid = false,
                    hasChanges = true,
                )
            }
        }
    }

    val onResetUiState: () -> Unit = {
        viewModelState.update {
            it.copy(
                newPrivkey = uiState.value.privkey,
                hasChanges = false,
                isInvalid = false,
            )
        }
    }

    companion object {
        fun provideFactory(
            profilePreferences: ProfilePreferences,
            context: Context,
            clip: ClipboardManager,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return KeysViewModel(
                        profilePreferences = profilePreferences,
                        context = context,
                        clip = clip
                    ) as T
                }
            }
    }
}
