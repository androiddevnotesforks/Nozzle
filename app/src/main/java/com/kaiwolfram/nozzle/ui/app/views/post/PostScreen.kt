package com.kaiwolfram.nozzle.ui.app.views.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nostrclientkt.Metadata
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.ClosableTopBar
import com.kaiwolfram.nozzle.ui.components.InputBox
import com.kaiwolfram.nozzle.ui.components.SendButton


@Composable
fun PostScreen(
    uiState: PostViewModelState,
    metadataState: Metadata?,
    onChangeContent: (String) -> Unit,
    onSendOrShowErrorToast: (String) -> Unit,
    onGoBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val errorToast = stringResource(id = R.string.your_post_is_empty)
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
        InputBox(
            picture = metadataState?.picture.orEmpty(),
            pubkey = uiState.pubkey,
            placeholder = stringResource(id = R.string.post_your_thoughts),
            onChangeInput = onChangeContent
        )
    }
}
