package com.kaiwolfram.nozzle.ui.app.views.keys

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.kaiwolfram.nozzle.R

@Composable
fun KeysScreen(
    uiState: KeysViewModelState,
    onCopyPubkeyAndShowToast: (Context, ClipboardManager, String) -> Unit,
) {
    Column {
        TopBar()
        Column(modifier = Modifier.padding(8.dp)) {
            Pubkey(
                pubkey = uiState.pubkey,
                onCopyPubkeyAndShowToast = onCopyPubkeyAndShowToast
            )
            Spacer(modifier = Modifier.height(8.dp))
            Privkey(privkey = uiState.privkey)
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.hasChanges) {
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { /*TODO*/ }) {
                    Text(text = "Update key pair")
                }
            }
        }
    }
}

@Composable
private fun TopBar() {
    TopAppBar() {
        Icon(
            modifier = Modifier.clickable { /*TODO*/ },
            imageVector = Icons.Default.ArrowBack,
            contentDescription = stringResource(id = R.string.return_back),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.keys),
            style = MaterialTheme.typography.h6,
        )
    }
}

@Composable
private fun Pubkey(
    pubkey: String,
    onCopyPubkeyAndShowToast: (Context, ClipboardManager, String) -> Unit
) {
    Text(
        text = stringResource(id = R.string.public_key),
        fontWeight = FontWeight.Bold
    )
    Text(text = "Your public key identifies your account. You can share it with anyone trying to find you.")
    TextField(
        value = TextFieldValue(pubkey),
        enabled = false,
        onValueChange = {},
        trailingIcon = { CopyIcon(onCopyPubkeyAndShowToast = onCopyPubkeyAndShowToast) }
    )
}

@Composable
private fun CopyIcon(onCopyPubkeyAndShowToast: (Context, ClipboardManager, String) -> Unit) {
    val toast = stringResource(id = R.string.copied_pubkey)
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    Icon(
        modifier = Modifier
            .size(18.dp)
            .clickable { onCopyPubkeyAndShowToast(context, clipboardManager, toast) },
        imageVector = Icons.Default.ContentCopy,
        contentDescription = stringResource(id = R.string.copy_content),
        tint = colors.onBackground
    )
}

@Composable
private fun VisibilityIcon(isVisible: Boolean) {
    Icon(
        modifier = Modifier
            .size(18.dp)
            .clickable { /* TODO */ },
        imageVector = if (isVisible) {
            Icons.Default.Visibility
        } else {
            Icons.Default.VisibilityOff
        },
        contentDescription = stringResource(id = R.string.toggle_visibility),
        tint = colors.onBackground
    )
}


@Composable
private fun Privkey(privkey: String) {
    Text(text = "Private key", fontWeight = FontWeight.Bold)
    Text(text = "Your private key verifies your identity.")
    Text(text = "Do not share it with anyone else!")
    TextField(
        value = TextFieldValue(privkey),
        visualTransformation = PasswordVisualTransformation(),
        onValueChange = {},
        trailingIcon = { VisibilityIcon(true) }
    )
}
