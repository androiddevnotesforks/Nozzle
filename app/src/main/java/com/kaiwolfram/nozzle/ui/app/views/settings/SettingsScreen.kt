package com.kaiwolfram.nozzle.ui.app.views.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.ActionButton
import com.kaiwolfram.nozzle.ui.components.ChangeableTextField
import com.kaiwolfram.nozzle.ui.components.TopBar
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun SettingsScreen(
    uiState: SettingsViewModelState,
    onUpdateProfileAndShowToast: (String) -> Unit,
    onChangeName: (String) -> Unit,
    onChangeBio: (String) -> Unit,
    onChangePictureUrl: (String) -> Unit,
    onResetUiState: () -> Unit,
    onGoBack: () -> Unit,
) {
    Column {
        TopBar(text = stringResource(id = R.string.settings), onGoBack = onGoBack)
        Column(modifier = Modifier.padding(spacing.screenEdge)) {
            Username(
                username = uiState.usernameInput,
                isInvalid = uiState.isInvalidUsername,
                onChangeName = onChangeName
            )
            Spacer(modifier = Modifier.height(spacing.xxl))

            Bio(bio = uiState.bioInput, onChangeBio = onChangeBio)
            Spacer(modifier = Modifier.height(spacing.xxl))

            ProfilePictureUrl(
                pictureUrl = uiState.pictureUrlInput,
                isInvalid = uiState.isInvalidPictureUrl,
                onChangePictureUrl = onChangePictureUrl
            )
            Spacer(modifier = Modifier.height(spacing.large))

            if (uiState.hasChanges) {
                val toast = stringResource(id = R.string.profile_updated)
                ActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.update_profile),
                    onAction = { onUpdateProfileAndShowToast(toast) },
                    clearFocusAfterAction = true
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
    onChangeName: (String) -> Unit,
) {
    Text(text = stringResource(id = R.string.username), fontWeight = FontWeight.Bold)
    ChangeableTextField(
        modifier = Modifier.fillMaxWidth(),
        value = username,
        isError = isInvalid,
        placeholder = stringResource(id = R.string.enter_your_username),
        errorLabel = stringResource(id = R.string.invalid_username),
        onChangeValue = onChangeName,
    )
}

@Composable
private fun Bio(
    bio: String,
    onChangeBio: (String) -> Unit,
) {
    Text(text = stringResource(id = R.string.about_you), fontWeight = FontWeight.Bold)
    ChangeableTextField(
        modifier = Modifier.fillMaxWidth(),
        value = bio,
        maxLines = 3,
        placeholder = stringResource(id = R.string.describe_yourself),
        errorLabel = stringResource(id = R.string.invalid_username),
        onChangeValue = onChangeBio,
    )
}

@Composable
private fun ProfilePictureUrl(
    pictureUrl: String,
    isInvalid: Boolean,
    onChangePictureUrl: (String) -> Unit,
) {
    Text(text = stringResource(id = R.string.profile_picture_url), fontWeight = FontWeight.Bold)
    ChangeableTextField(
        modifier = Modifier.fillMaxWidth(),
        value = pictureUrl,
        isError = isInvalid,
        maxLines = 3,
        placeholder = stringResource(id = R.string.enter_a_picture_url),
        errorLabel = stringResource(id = R.string.invalid_url),
        keyboardType = KeyboardType.Uri,
        onChangeValue = onChangePictureUrl,
    )
}
