package com.kaiwolfram.nozzle.ui.app.views.reply

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrService
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

private const val TAG = "ReplyViewModel"

data class ReplyViewModelState(
    val enabled: Boolean = false,
    val reply: String = "",
    val recipient: String = "",
    val pictureUrl: String = "https://robohash.org/kai",
    val pubkey: String = "kai",
)

class ReplyViewModel(
    private val nostrRepository: INostrService,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ReplyViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize ReplyViewModel")
    }

    val onSend: () -> Unit = {
        // TODO
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        fun provideFactory(
            nostrService: INostrService,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReplyViewModel(
                    nostrRepository = nostrService,
                ) as T
            }
        }
    }
}
