package com.kaiwolfram.nozzle.ui.app.views.keys

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.ActionButton
import com.kaiwolfram.nozzle.ui.components.CopyAndToastIcon
import com.kaiwolfram.nozzle.ui.components.TopBar
import com.kaiwolfram.nozzle.ui.components.VisibilityIcon
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun KeysScreen(
    uiState: KeysViewModelState,
    onCopyPubkeyAndShowToast: (String) -> Unit,
    onUpdateKeyPairAndShowToast: (String) -> Unit,
    onPrivkeyChange: (String) -> Unit,
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
            Spacer(modifier = Modifier.height(spacing.large))
            Privkey(
                privkey = uiState.privkey,
                isInvalid = uiState.isInvalid,
                onPrivkeyChange = onPrivkeyChange
            )
            Spacer(modifier = Modifier.height(spacing.xxl))
            if (uiState.hasChanges) {
                val toast = stringResource(id = R.string.key_pair_updated)
                ActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.update_key_pair),
                    onAction = { onUpdateKeyPairAndShowToast(toast) }
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
                toastText = stringResource(id = R.string.copied_pubkey),
                onCopyAndShowToast = onCopyPubkeyAndShowToast
            )
        }
    )
}

@Composable
private fun Privkey(
    privkey: String,
    isInvalid: Boolean,
    onPrivkeyChange: (String) -> Unit
) {
    Text(
        text = stringResource(id = R.string.private_key),
        fontWeight = FontWeight.Bold
    )
    Text(text = stringResource(id = R.string.private_key_description))
    Text(text = stringResource(id = R.string.private_key_warning))
    var isVisible by remember { mutableStateOf(false) }
    var newPrivkey by remember { mutableStateOf(TextFieldValue(privkey)) }
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = newPrivkey,
        isError = isInvalid,
        maxLines = 2,
        label = if (isInvalid) {
            { Text(text = stringResource(id = R.string.invalid_private_key)) }
        } else {
            null
        },
        placeholder = { Text(text = stringResource(id = R.string.enter_a_private_key)) },
        visualTransformation = if (!isVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        onValueChange = { newValue ->
            newPrivkey = newValue
            onPrivkeyChange(newValue.text)
        },
        trailingIcon = {
            VisibilityIcon(
                isVisible = isVisible,
                onToggle = { isVisible = !isVisible })
        }
    )
}
