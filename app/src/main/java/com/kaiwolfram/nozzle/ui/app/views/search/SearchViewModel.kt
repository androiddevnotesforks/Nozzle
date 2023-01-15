package com.kaiwolfram.nozzle.ui.app.views.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.utils.npubToHex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val TAG = "SearchViewModel"

data class SearchViewModelState(
    val input: String = "",
    val isInvalid: Boolean = false,
)

class SearchViewModel : ViewModel() {

    private val viewModelState = MutableStateFlow(SearchViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize SearchViewModel")
    }

    val onValidateAndNavigateToProfile: ((String) -> Unit) -> Unit = { onNavigateToProfile ->
        uiState.value.input.let { input ->
            val result = npubToHex(input)
            result.onSuccess { onNavigateToProfile(it) }
            result.onFailure {
                viewModelState.update { state -> state.copy(isInvalid = true) }
            }
        }
    }

    val onChangeInput: (String) -> Unit = { input ->
        uiState.value.let {
            viewModelState.update {
                it.copy(input = input)
            }
        }
    }

    val onResetUI: () -> Unit = {
        viewModelState.update {
            it.copy(input = "", isInvalid = false)
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel() as T
                }
            }
    }
}
