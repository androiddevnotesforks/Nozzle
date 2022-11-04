package com.kaiwolfram.nozzle.ui.app.feed

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime

private const val TAG = "FeedViewModel"

data class FeedViewModelState(
    val posts: List<Post> = listOf(),
)

class FeedViewModel : ViewModel() {
    private val viewModelState = MutableStateFlow(FeedViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )


    init {
        Log.i(TAG, "Initialize FeedViewModel")
        val postMock = Post(
            author = "Kai Wolfram",
            profilePic = Icons.Rounded.Call,
            published = LocalDateTime.now(),
            body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit."
        )
        viewModelState.update {
            it.copy(
                posts = listOf(postMock, postMock, postMock, postMock),
            )
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel() as T
            }
        }
    }
}
