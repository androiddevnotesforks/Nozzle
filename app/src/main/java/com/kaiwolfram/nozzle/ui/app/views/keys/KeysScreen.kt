package com.kaiwolfram.nozzle.ui.app.views.keys

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
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
        TopAppBar {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            Text(text = stringResource(id = R.string.keys))
        }
        Text(text = "Public key", fontWeight = FontWeight.Bold)
        Text(text = "Your public key is used to identify your account. You can share it with people trying to find you.")
        Text(text = "This is linked to your private key and can't be independently changed")
        TextField(
            value = TextFieldValue(uiState.pubkey),
            enabled = false,
            onValueChange = {},
        )
        Text(text = "Private key", fontWeight = FontWeight.Bold)
        Text(text = "Your private key is used to verify your identity. Do not share it with anyone else!")
        TextField(
            value = TextFieldValue(uiState.privkey),
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = {},
        )
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Save new key")
        }
        // TODO: Open dialog when navigating to confirm/discard changes to priv key
    }
}
