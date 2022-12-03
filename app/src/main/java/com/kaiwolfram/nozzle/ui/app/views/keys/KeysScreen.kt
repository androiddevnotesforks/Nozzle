package com.kaiwolfram.nozzle.ui.app.views.keys

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import com.kaiwolfram.nozzle.R

@Composable
fun KeysScreen(
    uiState: KeysViewModelState,
) {
    Column {
        TopAppBar(backgroundColor = colors.background) {
            Text(text = stringResource(id = R.string.keys))
        }
        Text(text = "Public key", fontWeight = FontWeight.Bold)
        Text(text = "Your public key identifies your account. You can share it with anyone trying to find you.")
        TextField(
            value = TextFieldValue(uiState.pubkey),
            enabled = false,
            onValueChange = {},
        )
        Text(text = "Private key", fontWeight = FontWeight.Bold)
        Text(text = "Your private key verifies your identity.")
        Text(text = "Do not share it with anyone else!")
        TextField(
            value = TextFieldValue(uiState.privkey),
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = {},
        )
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Update key pair")
        }
        // TODO: Open dialog when navigating to confirm/discard changes to priv key
    }
}
