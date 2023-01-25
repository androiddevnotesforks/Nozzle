package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import com.kaiwolfram.nozzle.ui.theme.sizing
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun InputBox(
    picture: String,
    pubkey: String,
    placeholder: String,
    onChangeInput: (String) -> Unit,
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
                pictureUrl = picture,
                pubkey = pubkey
            )
            ChangeableTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester),
                maxLines = Int.MAX_VALUE,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = placeholder,
                onChangeValue = onChangeInput,
                keyboardImeAction = ImeAction.Default
            )
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
