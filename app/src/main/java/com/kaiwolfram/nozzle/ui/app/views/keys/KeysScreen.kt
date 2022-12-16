package com.kaiwolfram.nozzle.ui.app.views.keys

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.*
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun KeysScreen(
    uiState: KeysViewModelState,
    onCopyPubkeyAndShowToast: (String) -> Unit,
    onCopyPrivkeyAndShowToast: (String) -> Unit,
    onUpdateKeyPairAndShowToast: (String) -> Unit,
    onChangePrivkey: (String) -> Unit,
    onUpdateDrawerPubkey: () -> Unit,
    onResetUiState: () -> Unit,
    onNavigateToFeed: () -> Unit,
) {
    Column {
        TopBar(text = stringResource(id = R.string.keys), onGoBack = onNavigateToFeed)
        Column(modifier = Modifier.padding(spacing.screenEdge)) {
            Pubkey(
                pubkey = uiState.pubkey,
                onCopyPubkeyAndShowToast = onCopyPubkeyAndShowToast
            )
            Spacer(modifier = Modifier.height(spacing.xxl))
            Privkey(
                privkey = uiState.privkeyInput,
                isInvalid = uiState.isInvalid,
                onChangePrivkey = onChangePrivkey,
                onCopyPrivkeyAndShowToast = onCopyPrivkeyAndShowToast
            )
            Spacer(modifier = Modifier.height(spacing.large))
            if (uiState.hasChanges) {
                val toast = stringResource(id = R.string.key_pair_updated)
                ActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.update_key_pair),
                    onAction = {
                        onUpdateKeyPairAndShowToast(toast)
                        onUpdateDrawerPubkey()
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
private fun Pubkey(
    pubkey: String,
    onCopyPubkeyAndShowToast: (String) -> Unit
) {
    Text(
        text = stringResource(id = R.string.public_key),
        fontWeight = FontWeight.Bold
    )
    Text(text = stringResource(id = R.string.public_key_explanation))
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = TextFieldValue(pubkey),
        enabled = false,
        onValueChange = { /* Always disabled*/ },
        trailingIcon = {
            CopyAndToastIcon(
                toastText = stringResource(id = R.string.pubkey_copied),
                onCopyAndShowToast = onCopyPubkeyAndShowToast
            )
        }
    )
}

@Composable
private fun Privkey(
    privkey: String,
    isInvalid: Boolean,
    onChangePrivkey: (String) -> Unit,
    onCopyPrivkeyAndShowToast: (String) -> Unit,
) {
    var isVisible by remember { mutableStateOf(false) }
    Text(
        text = stringResource(id = R.string.private_key),
        fontWeight = FontWeight.Bold
    )
    Text(text = stringResource(id = R.string.private_key_description))
    Text(text = stringResource(id = R.string.private_key_warning))
    ChangeableTextField(
        modifier = Modifier.fillMaxWidth(),
        value = privkey,
        isError = isInvalid,
        maxLines = 2,
        placeholder = stringResource(id = R.string.enter_a_private_key),
        errorLabel = stringResource(id = R.string.invalid_private_key),
        isPassword = !isVisible,
        onChangeValue = onChangePrivkey,
        trailingIcon = {
            CopyAndVisibilityIcons(isVisible = isVisible,
                onCopyPrivkeyAndShowToast = onCopyPrivkeyAndShowToast,
                onToggleVisibility = { isVisible = !isVisible }
            )
        },
    )
}

@Composable
private fun CopyAndVisibilityIcons(
    isVisible: Boolean,
    onCopyPrivkeyAndShowToast: (String) -> Unit,
    onToggleVisibility: () -> Unit
) {
    Row {
        VisibilityIcon(
            isVisible = isVisible,
            onToggle = { onToggleVisibility() })
        Spacer(modifier = Modifier.width(spacing.small))
        CopyAndToastIcon(
            toastText = stringResource(id = R.string.privkey_copied),
            onCopyAndShowToast = onCopyPrivkeyAndShowToast
        )
    }
}
