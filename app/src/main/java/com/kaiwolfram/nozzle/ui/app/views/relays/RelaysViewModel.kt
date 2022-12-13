package com.kaiwolfram.nozzle.ui.app.views.relays

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val TAG = "RelaysViewModel"

data class RelaysViewModelState(
    val activeRelays: List<String> = listOf(
        "coming",
        "soon",
        "maybe",
        "2weeks",
        "coming",
        "soon",
        "maybe",
        "2weeks",
        "coming",
        "soon",
        "maybe",
        "2weeks"
    ),
    val inactiveRelays: List<String> = listOf("lol", "lmao", "never", "ever")
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

    val onLeaveRelay: (Int) -> Unit = { index ->
        Log.i(TAG, "Leave index $index")
        if (uiState.value.activeRelays.size > index) {
            val active = uiState.value.activeRelays.toMutableList()
            val inactive = uiState.value.inactiveRelays.toMutableList()
            val removed = active.removeAt(index)
            inactive.add(0, removed)
            viewModelState.update {
                it.copy(
                    activeRelays = active,
                    inactiveRelays = inactive
                )
            }
        }
    }

    val onJoinRelay: (Int) -> Unit = { index ->
        Log.i(TAG, "Join index $index")
        if (uiState.value.inactiveRelays.size > index) {
            val active = uiState.value.activeRelays.toMutableList()
            val inactive = uiState.value.inactiveRelays.toMutableList()
            val removed = inactive.removeAt(index)
            active.add(removed)
            viewModelState.update {
                it.copy(
                    activeRelays = active,
                    inactiveRelays = inactive
                )
            }
        }
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
