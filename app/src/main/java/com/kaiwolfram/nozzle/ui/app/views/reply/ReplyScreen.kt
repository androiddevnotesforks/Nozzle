package com.kaiwolfram.nozzle.ui.app.views.reply

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.*
import com.kaiwolfram.nozzle.ui.theme.sizing
import com.kaiwolfram.nozzle.ui.theme.spacing


@Composable
fun ReplyScreen(
    uiState: ReplyViewModelState,
    onChangeReply: (String) -> Unit,
    onSend: () -> Unit,
    onGoBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ReplyTopBar(enabled = uiState.isSendable, onSend = onSend, onGoBack = onGoBack)
        ReplyingTo(
            modifier = Modifier.padding(top = spacing.medium, start = spacing.screenEdge),
            name = uiState.recipientName
        )
        ReplyBox(
            pictureUrl = uiState.pictureUrl,
            pubkey = uiState.pubkey,
            onChangeReply = onChangeReply
        )
    }
}

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
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            ProfilePicture(
                modifier = Modifier
                    .padding(start = spacing.screenEdge, top = spacing.large)
                    .size(sizing.profilePicture),
                pictureUrl = pictureUrl,
                pubkey = pubkey
            )
            ChangeableTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester),
                maxLines = Int.MAX_VALUE,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = colors.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = stringResource(id = R.string.post_your_reply),
                onChangeValue = onChangeReply
            )
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
