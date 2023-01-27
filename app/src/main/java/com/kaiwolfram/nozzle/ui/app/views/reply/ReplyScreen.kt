package com.kaiwolfram.nozzle.ui.app.views.reply

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.ClosableTopBar
import com.kaiwolfram.nozzle.ui.components.InputBox
import com.kaiwolfram.nozzle.ui.components.SendTopBarButton
import com.kaiwolfram.nozzle.ui.components.text.ReplyingTo
import com.kaiwolfram.nozzle.ui.theme.spacing


@Composable
fun ReplyScreen(
    uiState: ReplyViewModelState,
    metadataState: Metadata?,
    onChangeReply: (String) -> Unit,
    onSend: () -> Unit,
    onGoBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ClosableTopBar(
            onClose = onGoBack,
            trailingIcon = {
                SendTopBarButton(
                    isSendable = uiState.isSendable,
                    onSend = { onSend() },
                    onGoBack = onGoBack,
                )
            }
        )
        ReplyingTo(
            modifier = Modifier.padding(top = spacing.medium, start = spacing.screenEdge),
            name = uiState.recipientName
        )
        InputBox(
            picture = metadataState?.picture.orEmpty(),
            pubkey = uiState.pubkey,
            placeholder = stringResource(id = R.string.post_your_reply),
            onChangeInput = onChangeReply
        )
    }
}
