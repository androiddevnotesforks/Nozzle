package com.kaiwolfram.nozzle.ui.app.views.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

private const val TAG = "ChatViewModel"

data class ChatViewModelState(
    val label: String = "Chat is coming soon!",
)

class ChatViewModel : ViewModel() {
    private val viewModelState = MutableStateFlow(ChatViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )


    init {
        Log.i(TAG, "Initialize ChatViewModel")
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChatViewModel() as T
            }
        }
    }
}
