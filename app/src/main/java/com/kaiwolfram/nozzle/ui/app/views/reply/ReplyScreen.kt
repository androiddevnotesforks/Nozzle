package com.kaiwolfram.nozzle.ui.app.views.reply

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.ClosableTopBar
import com.kaiwolfram.nozzle.ui.components.InputBox
import com.kaiwolfram.nozzle.ui.components.ReplyingTo
import com.kaiwolfram.nozzle.ui.components.SendButton
import com.kaiwolfram.nozzle.ui.theme.spacing


@Composable
fun ReplyScreen(
    uiState: ReplyViewModelState,
    onChangeReply: (String) -> Unit,
    onSendOrShowErrorToast: (String) -> Unit,
    onGoBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val errorToast = stringResource(id = R.string.your_reply_is_empty)
        ClosableTopBar(
            onClose = onGoBack,
            trailingIcon = {
                SendButton(
                    isSendable = uiState.isSendable,
                    onSend = { onSendOrShowErrorToast(errorToast) },
                    onGoBack = onGoBack,
                )
            }
        )
        ReplyingTo(
            modifier = Modifier.padding(top = spacing.medium, start = spacing.screenEdge),
            name = uiState.recipientName
        )
        InputBox(
            pictureUrl = uiState.pictureUrl,
            pubkey = uiState.pubkey,
            placeholder = stringResource(id = R.string.post_your_reply),
            onChangeInput = onChangeReply
        )
    }
}
