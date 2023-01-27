package com.kaiwolfram.nozzle.ui.app.views.post

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.provider.IPersonalProfileProvider
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PostViewModel"

data class PostViewModelState(
    val content: String = "",
    val isSendable: Boolean = false,
    val pubkey: String = "",
)

class PostViewModel(
    private val personalProfileProvider: IPersonalProfileProvider,
    private val nostrService: INostrService,
    private val postDao: PostDao,
    context: Context,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(PostViewModelState())

    val metadataState = personalProfileProvider.getMetadata()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            null
        )

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize PostViewModel")
    }

    val onPreparePost: () -> Unit = {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.i(TAG, "Prepare new post")
            viewModelState.update {
                it.copy(
                    pubkey = personalProfileProvider.getPubkey(),
                    content = "",
                    isSendable = false,
                )
            }
        }
    }

    val onChangeContent: (String) -> Unit = { input ->
        if (input != uiState.value.content) {
            viewModelState.update {
                it.copy(content = input, isSendable = input.isNotBlank())
            }
        }
    }

    val onSend: () -> Unit = {
        uiState.value.let { state ->
            if (!state.isSendable) {
                Log.i(TAG, "Post is not sendable")
            } else {
                Log.i(TAG, "Send post")
                val event = nostrService.sendPost(content = state.content)
                viewModelScope.launch(context = Dispatchers.IO) {
                    postDao.insertIfNotPresent(PostEntity.fromEvent(event))
                }
                Toast.makeText(
                    context,
                    context.getString(R.string.post_published),
                    Toast.LENGTH_SHORT
                ).show()
                resetUI()
            }
        }
    }

    private fun resetUI() {
        viewModelState.update {
            it.copy(content = "", isSendable = false)
        }
    }

    companion object {
        fun provideFactory(
            personalProfileProvider: IPersonalProfileProvider,
            nostrService: INostrService,
            postDao: PostDao,
            context: Context
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PostViewModel(
                    nostrService = nostrService,
                    personalProfileProvider = personalProfileProvider,
                    postDao = postDao,
                    context = context,
                ) as T
            }
        }
    }
}
