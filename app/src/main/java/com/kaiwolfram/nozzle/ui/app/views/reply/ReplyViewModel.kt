package com.kaiwolfram.nozzle.ui.app.views.reply

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nostrclientkt.ReplyTo
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.provider.IPersonalProfileProvider
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
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
    val pubkey: String = "",
)

class ReplyViewModel(
    private val nostrService: INostrService,
    private val personalProfileProvider: IPersonalProfileProvider,
    private val postDao: PostDao,
    context: Context,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ReplyViewModelState())
    private var recipientPubkey: String = ""
    private var postToReplyTo: PostWithMeta? = null

    var metadataState = personalProfileProvider.getMetadata()
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
        Log.i(TAG, "Initialize ReplyViewModel")
    }

    val onPrepareReply: (PostWithMeta) -> Unit = { post ->
        postToReplyTo = post
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.i(TAG, "Set reply to ${post.pubkey}")
            viewModelState.update {
                recipientPubkey = post.pubkey
                it.copy(
                    recipientName = post.name,
                    pubkey = personalProfileProvider.getPubkey(),
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
                Log.i(TAG, "Reply is not sendable")
                Toast.makeText(context, errorToast, Toast.LENGTH_SHORT).show()
            } else {
                postToReplyTo?.let {
                    Log.i(TAG, "Send reply to ${state.recipientName} ${state.pubkey}")
                    val replyTo = ReplyTo(
                        replyToRoot = it.replyToRootId,
                        replyTo = it.id,
                        relayUrl = "",
                    )
                    val event = nostrService.sendReply(
                        replyTo = replyTo,
                        content = state.reply
                    )
                    viewModelScope.launch(context = Dispatchers.IO) {
                        postDao.insertIfNotPresent(PostEntity.fromEvent(event))
                    }
                }
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
            personalProfileProvider: IPersonalProfileProvider,
            postDao: PostDao,
            context: Context
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReplyViewModel(
                    nostrService = nostrService,
                    personalProfileProvider = personalProfileProvider,
                    postDao = postDao,
                    context = context,
                ) as T
            }
        }
    }
}
