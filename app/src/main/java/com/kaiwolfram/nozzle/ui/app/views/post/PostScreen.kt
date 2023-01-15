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
import com.kaiwolfram.nozzle.ui.components.SendTopBarButton


@Composable
fun PostScreen(
    uiState: PostViewModelState,
    metadataState: Metadata?,
    onChangeContent: (String) -> Unit,
    onSend: (String) -> Unit,
    onGoBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val toast = stringResource(id = R.string.post_published)
        ClosableTopBar(
            onClose = onGoBack,
            trailingIcon = {
                SendTopBarButton(
                    isSendable = uiState.isSendable,
                    onSend = { onSend(toast) },
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
