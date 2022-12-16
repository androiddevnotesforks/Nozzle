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
private const val MAX_RELAYS = 5

data class RelaysViewModelState(
    val relays: List<String> = listOf(),
    val showAddButton: Boolean = true,
    val relayInput: String = ""
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
        val showAddButton = uiState.value.relays.size < MAX_RELAYS
        viewModelState.update {
            it.copy(
                relays = listOf("lol", "lmao", "xD"),
                showAddButton = showAddButton,
                relayInput = ""
            )
        }

    }

    val onRemoveRelay: (Int) -> Unit = { index ->
        if (index > 0 && uiState.value.relays.size > index) {
            Log.i(TAG, "Leave index $index")
            val active = uiState.value.relays.toMutableList()
            active.removeAt(index)
            viewModelState.update {
                it.copy(
                    relays = active,
                )
            }
        }
    }

    val onAddRelay: () -> Unit = {
        uiState.value.run {
            if (!relays.contains(relayInput)) {
                val newList = relays.toMutableList()
                newList.add(relayInput)
                viewModelState.update {
                    it.copy(
                        relays = newList,
                        relayInput = "",
                    )
                }
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
