package com.kaiwolfram.nozzle.ui.app.views.reply

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kaiwolfram.nozzle.ui.components.ChangeableTextField
import com.kaiwolfram.nozzle.ui.components.CloseButton
import com.kaiwolfram.nozzle.ui.components.ProfilePicture
import com.kaiwolfram.nozzle.ui.components.SendButton
import com.kaiwolfram.nozzle.ui.theme.sizing


@Composable
fun ReplyScreen(
    uiState: ReplyViewModelState,
    onSend: () -> Unit,
    onGoBack: () -> Unit,
) {
    Column {
        ReplyTopBar(enabled = uiState.enabled, onSend = onSend, onGoBack = onGoBack)
        ReplyBox(
            pictureUrl = uiState.pictureUrl,
            pubkey = uiState.pubkey,
            onChangeReply = {/*TODO*/ })
    }
}

// TODO: Use TopBar from components
@Composable
private fun ReplyTopBar(enabled: Boolean, onSend: () -> Unit, onGoBack: () -> Unit) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colors.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CloseButton(onGoBack = onGoBack)
            SendButton(enabled = enabled, onSend = onSend, onGoBack = onGoBack)
        }
    }
}

@Composable
private fun ReplyBox(
    pictureUrl: String,
    pubkey: String,
    onChangeReply: (String) -> Unit,
) {
    Row {
        ProfilePicture(
            modifier = Modifier.size(sizing.profilePicture),
            pictureUrl = pictureUrl,
            pubkey = pubkey
        )
        ChangeableTextField(modifier = Modifier.fillMaxSize(), onChangeValue = onChangeReply)
    }
}
