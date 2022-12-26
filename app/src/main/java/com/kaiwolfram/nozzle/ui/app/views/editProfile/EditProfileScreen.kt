package com.kaiwolfram.nozzle.ui.app.views.editProfile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.ChangeableTextField
import com.kaiwolfram.nozzle.ui.components.CheckButton
import com.kaiwolfram.nozzle.ui.components.ReturnableTopBar
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun EditProfileScreen(
    uiState: EditProfileViewModelState,
    onUpdateProfileAndShowToast: (String) -> Unit,
    onChangeName: (String) -> Unit,
    onChangeBio: (String) -> Unit,
    onChangePictureUrl: (String) -> Unit,
    onChangeNip05: (String) -> Unit,
    onResetUiState: () -> Unit,
    onCanGoBack: () -> Boolean,
    onGoBack: () -> Unit,
) {
    Column {
        val toast = stringResource(id = R.string.profile_updated)
        ReturnableTopBar(
            text = stringResource(id = R.string.edit_profile),
            onGoBack = onGoBack,
            trailingIcon = {
                CheckButton(
                    hasChanges = uiState.hasChanges,
                    onCheck = { onUpdateProfileAndShowToast(toast) },
                    onCanGoBack = onCanGoBack,
                    onGoBack = onGoBack,
                )
            }
        )
        Column(
            modifier = Modifier
                .padding(spacing.screenEdge)
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
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
            Spacer(modifier = Modifier.height(spacing.xxl))

            Nip05(
                nip05 = uiState.nip05Input,
                onChangeNip05 = onChangeNip05
            )
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
        keyboardImeAction = ImeAction.Next,
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
        keyboardImeAction = ImeAction.Next,
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
        keyboardImeAction = ImeAction.Next,
        onChangeValue = onChangePictureUrl,
    )
}

@Composable
private fun Nip05(
    nip05: String,
    onChangeNip05: (String) -> Unit,
) {
    Text(text = stringResource(id = R.string.nip05_identifier), fontWeight = FontWeight.Bold)
    ChangeableTextField(
        modifier = Modifier.fillMaxWidth(),
        value = nip05,
        maxLines = 3,
        placeholder = stringResource(id = R.string.enter_nip05),
        keyboardType = KeyboardType.Uri,
        onChangeValue = onChangeNip05,
    )
}
