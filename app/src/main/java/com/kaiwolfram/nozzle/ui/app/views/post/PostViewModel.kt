package com.kaiwolfram.nozzle.ui.app.views.post

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nostrclientkt.model.AllRelays
import com.kaiwolfram.nostrclientkt.model.MultipleRelays
import com.kaiwolfram.nostrclientkt.model.RelaySelection
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.provider.IPersonalProfileProvider
import com.kaiwolfram.nozzle.data.room.dao.EventRelayDao
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.dao.RelayDao
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.data.utils.listRelayStatuses
import com.kaiwolfram.nozzle.data.utils.toggleRelay
import com.kaiwolfram.nozzle.model.RelayActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PostViewModel"

data class PostViewModelState(
    val content: String = "",
    val pubkey: String = "",
    val relayStatuses: List<RelayActive> = listOf(),
    val isSendable: Boolean = false,
)

class PostViewModel(
    private val personalProfileProvider: IPersonalProfileProvider,
    private val nostrService: INostrService,
    private val postDao: PostDao,
    private val eventRelayDao: EventRelayDao,
    relayDao: RelayDao,
    context: Context,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(PostViewModelState())

    var metadataState = personalProfileProvider.getMetadata()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    private val relayState = relayDao.listRelays()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            listOf()
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

    val onPreparePost: (RelaySelection) -> Unit = { relaySelection ->
        metadataState = personalProfileProvider.getMetadata()
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                null
            )

        viewModelScope.launch(context = Dispatchers.IO) {
            Log.i(TAG, "Prepare new post")
            viewModelState.update {
                it.copy(
                    pubkey = personalProfileProvider.getPubkey(),
                    content = "",
                    isSendable = false,
                    relayStatuses = listRelayStatuses(
                        allRelayUrls = relayState.value,
                        relaySelection = relaySelection
                    ),
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

    val onToggleRelaySelection: (Int) -> Unit = { index ->
        val toggled = toggleRelay(relays = uiState.value.relayStatuses, index = index)
        if (toggled.any { it.isActive }) {
            viewModelState.update {
                it.copy(relayStatuses = toggled)
            }
        }
    }

    val onSend: () -> Unit = {
        uiState.value.let { state ->
            val err = getErrorText(context = context, state = state)
            if (err != null) {
                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
            } else {
                val selectedRelays = state.relayStatuses.filter { it.isActive }.map { it.relayUrl }
                Log.i(TAG, "Send post to ${selectedRelays.size} relays")
                val event = nostrService.sendPost(
                    content = state.content,
                    relaySelection = MultipleRelays(selectedRelays)
                )
                viewModelScope.launch(context = Dispatchers.IO) {
                    postDao.insertIfNotPresent(PostEntity.fromEvent(event))
                    for (relay in selectedRelays) {
                        eventRelayDao.insertOrIgnore(eventId = event.id, relayUrl = relay)
                    }
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

    private fun getErrorText(context: Context, state: PostViewModelState): String? {
        return if (state.content.isBlank()) {
            context.getString(R.string.your_post_is_empty)
        } else if (state.relayStatuses.all { !it.isActive }) {
            context.getString(R.string.pls_select_relays)
        } else {
            null
        }
    }

    private fun resetUI() {
        viewModelState.update {
            it.copy(
                content = "",
                relayStatuses = listRelayStatuses(
                    allRelayUrls = relayState.value,
                    relaySelection = AllRelays
                ),
                isSendable = false,
                pubkey = personalProfileProvider.getPubkey()
            )
        }
    }

    companion object {
        fun provideFactory(
            personalProfileProvider: IPersonalProfileProvider,
            nostrService: INostrService,
            postDao: PostDao,
            eventRelayDao: EventRelayDao,
            relayDao: RelayDao,
            context: Context
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PostViewModel(
                    nostrService = nostrService,
                    personalProfileProvider = personalProfileProvider,
                    postDao = postDao,
                    eventRelayDao = eventRelayDao,
                    relayDao = relayDao,
                    context = context,
                ) as T
            }
        }
    }
}
