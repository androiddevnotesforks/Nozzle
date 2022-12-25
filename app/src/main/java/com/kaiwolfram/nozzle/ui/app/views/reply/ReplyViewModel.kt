package com.kaiwolfram.nozzle.ui.app.views.reply

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.preferences.profile.IProfileCache
import com.kaiwolfram.nozzle.model.PostWithMeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ReplyViewModel"

data class ReplyViewModelState(
    val recipientName: String = "",
    val reply: String = "",
    val isSendable: Boolean = false,
    val pictureUrl: String = "",
    val pubkey: String = "",
)

class ReplyViewModel(
    private val nostrService: INostrService,
    private val profileCache: IProfileCache,
    context: Context,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ReplyViewModelState())
    private var recipientPubkey: String = ""

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize ReplyViewModel")
    }

    val onPrepareReply: (PostWithMeta) -> Unit = { post ->
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.i(TAG, "Setting reply to ${post.pubkey}")
            viewModelState.update {
                recipientPubkey = post.pubkey
                it.copy(
                    recipientName = post.name,
                    pictureUrl = post.pictureUrl,
                    pubkey = profileCache.getPubkey(),
                    reply = "",
                    isSendable = false,
                )
            }
        }
    }

    val onChangeReply: (String) -> Unit = { input ->
        if (input != uiState.value.reply) {
            viewModelState.update {
                it.copy(reply = input, isSendable = input.isNotBlank())
            }
        }
    }

    val onSendOrShowErrorToast: (String) -> Unit = { errorToast ->
        uiState.value.let { state ->
            if (!state.isSendable) {
                Toast.makeText(context, errorToast, Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "Sending reply to ${state.recipientName} ${state.pubkey}")
                nostrService.send(
                    recipientPubkey = recipientPubkey,
                    reply = state.reply
                )
                reset()
            }
        }
    }

    private fun reset() {
        viewModelState.update {
            recipientPubkey = ""
            it.copy(
                recipientName = "",
                reply = "",
                isSendable = false,
            )
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        fun provideFactory(
            nostrService: INostrService,
            profileCache: IProfileCache,
            context: Context
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReplyViewModel(
                    nostrService = nostrService,
                    profileCache = profileCache,
                    context = context,
                ) as T
            }
        }
    }
}
