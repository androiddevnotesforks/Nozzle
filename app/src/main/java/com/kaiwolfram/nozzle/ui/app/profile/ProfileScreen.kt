package com.kaiwolfram.nozzle.ui.app.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.kaiwolfram.nozzle.R

@Composable
fun ProfileScreen(
    uiState: ProfileViewModelState,
    onChangeProfilePictureUrl: (String) -> Unit,
) {
    var openProfilePicDialog by remember { mutableStateOf(false) }
    if (openProfilePicDialog) {
        ChangeProfilePictureDialog(
            currentUrl = uiState.profilePictureUrl,
            onChangeUrl = onChangeProfilePictureUrl,
            onCloseDialog = { openProfilePicDialog = false })
    }
    Row {
        Icon(
            imageVector = uiState.profilePicture,
            contentDescription = stringResource(id = R.string.profile_picture),
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable { openProfilePicDialog = true }

        )
        Column {
            Text(text = uiState.name, fontWeight = FontWeight.Bold)
            Text(
                text = uiState.shortenedPubKey,
                color = Color.Gray.copy(alpha = 0.8f),
            )
            Text(text = uiState.bio)
        }
    }
}

@Composable
private fun ChangeProfilePictureDialog(
    currentUrl: String,
    onChangeUrl: (String) -> Unit,
    onCloseDialog: () -> Unit
) {
    var url by remember { mutableStateOf(currentUrl) }
    AlertDialog(
        shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = onCloseDialog,
        confirmButton = {
            TextButton(onClick = {
                onChangeUrl(url)
                onCloseDialog()
            }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onCloseDialog) {
                Text(text = stringResource(id = R.string.dismiss))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.set_new_profile_picture_url))
        },
        text = {
            Column {
                ProfilePictureUrlField(
                    currentUrl = url,
                    onChangeUrl = { change -> url = change })
            }
        },
    )
}

@Composable
private fun ProfilePictureUrlField(
    currentUrl: String,
    onChangeUrl: (String) -> Unit
) {
    var url by remember { mutableStateOf(TextFieldValue(currentUrl)) }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = url,
        onValueChange = {
            url = it
            onChangeUrl(it.text)
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Done,
            autoCorrect = false,
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        singleLine = true,
    )
}
