package com.kaiwolfram.nozzle.ui.app.views.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.ContentCreationTopBar
import com.kaiwolfram.nozzle.ui.components.InputBox


@Composable
fun PostScreen(
    uiState: PostViewModelState,
    metadataState: Metadata?,
    onChangeContent: (String) -> Unit,
    onToggleRelaySelection: (Int) -> Unit,
    onSend: () -> Unit,
    onGoBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ContentCreationTopBar(
            relaySelection = uiState.relaySelection,
            isSendable = uiState.isSendable,
            onToggleRelaySelection = onToggleRelaySelection,
            onSend = onSend,
            onClose = onGoBack
        )
        InputBox(
            picture = metadataState?.picture.orEmpty(),
            pubkey = uiState.pubkey,
            placeholder = stringResource(id = R.string.post_your_thoughts),
            onChangeInput = onChangeContent
        )
    }
}
