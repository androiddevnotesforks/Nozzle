package com.kaiwolfram.nozzle.ui.app.views.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.ActionButton
import com.kaiwolfram.nozzle.ui.components.TopBar
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun SettingsScreen(
    uiState: SettingsViewModelState,
    onUpdateProfileAndShowToast: (String) -> Unit,
    onUpdateDrawerName: () -> Unit,
    onResetUiState: () -> Unit,
    onNavigateToFeed: () -> Unit,
) {
    Column {
        TopBar(text = stringResource(id = R.string.settings), onGoBack = onNavigateToFeed)
        Column(modifier = Modifier.padding(spacing.screenEdge)) {
            Username(
                username = uiState.username,
                isInvalid = uiState.usernameIsInvalid,
                onNameChange = { /*TODO*/ })
            Spacer(modifier = Modifier.height(spacing.xxl))

            Bio(bio = uiState.bio, onBioChange = { /*TODO*/ })
            Spacer(modifier = Modifier.height(spacing.xxl))

            ProfilePictureUrl(
                pictureUrl = uiState.pictureUrl,
                isInvalid = uiState.pictureUrlIsInvalid,
                onPictureUrlChange = {/*TODO*/ })
            Spacer(modifier = Modifier.height(spacing.large))

            if (uiState.hasChanges) {
                val toast = stringResource(id = R.string.profile_updated)
                ActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.update_profile),
                    onAction = {
                        onUpdateProfileAndShowToast(toast)
                        onUpdateDrawerName()
                    }
                )
            }
        }
    }
    DisposableEffect(key1 = null) {
        onDispose { onResetUiState() }
    }
}

@Composable
private fun Username(
    username: String,
    isInvalid: Boolean,
    onNameChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Text(text = stringResource(id = R.string.username), fontWeight = FontWeight.Bold)
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = TextFieldValue(username),
        isError = isInvalid,
        maxLines = 1,
        placeholder = { Text(text = stringResource(id = R.string.enter_your_username)) },
        onValueChange = { onNameChange(username) },
        label = if (isInvalid) {
            { Text(text = stringResource(id = R.string.invalid_username)) }
        } else {
            null
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            autoCorrect = false,
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
    )
}

@Composable
private fun Bio(
    bio: String,
    onBioChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Text(text = stringResource(id = R.string.about_you), fontWeight = FontWeight.Bold)
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = TextFieldValue(bio),
        maxLines = 3,
        placeholder = { Text(text = stringResource(id = R.string.describe_yourself)) },
        onValueChange = { onBioChange(bio) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            autoCorrect = false,
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
    )
}

@Composable
private fun ProfilePictureUrl(
    pictureUrl: String,
    isInvalid: Boolean,
    onPictureUrlChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Text(text = stringResource(id = R.string.profile_picture_url), fontWeight = FontWeight.Bold)
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = TextFieldValue(pictureUrl),
        isError = isInvalid,
        maxLines = 2,
        placeholder = { Text(text = stringResource(id = R.string.enter_a_picture_url)) },
        label = if (isInvalid) {
            { Text(text = stringResource(id = R.string.invalid_url)) }
        } else {
            null
        },
        onValueChange = { onPictureUrlChange(pictureUrl) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Done,
            autoCorrect = false,
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
    )
}
