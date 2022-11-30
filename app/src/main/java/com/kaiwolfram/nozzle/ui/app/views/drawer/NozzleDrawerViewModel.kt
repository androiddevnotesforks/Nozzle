package com.kaiwolfram.nozzle.ui.app.views.drawer


import android.util.Log
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.utils.createEmptyPainter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val TAG = "NozzleDrawerViewModel"

data class NozzleDrawerViewModelState(
    val profilePicture: Painter = createEmptyPainter(),
    val profileName: String = "LOL",
)

class NozzleDrawerViewModel(private val defaultProfilePicture: Painter) : ViewModel() {
    private val viewModelState = MutableStateFlow(NozzleDrawerViewModelState())
    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize NozzleDrawerViewModel")
        viewModelState.update {
            it.copy(
                profilePicture = defaultProfilePicture
            )
        }
    }

    companion object {
        fun provideFactory(
            defaultProfilePicture: Painter,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NozzleDrawerViewModel(
                        defaultProfilePicture = defaultProfilePicture,
                    ) as T
                }
            }
    }
}
