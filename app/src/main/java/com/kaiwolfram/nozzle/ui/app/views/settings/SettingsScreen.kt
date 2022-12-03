package com.kaiwolfram.nozzle.ui.app.views.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import com.kaiwolfram.nozzle.R

@Composable
fun SettingsScreen(
    uiState: SettingsViewModelState,
) {
    Column {
        TopAppBar(backgroundColor = MaterialTheme.colors.background) {
            Text(text = stringResource(id = R.string.settings))
        }
        Text(text = "Username", fontWeight = FontWeight.Bold)
        TextField(
            value = TextFieldValue(uiState.name),
            enabled = false,
            onValueChange = {},
        )
        Text(text = "About you", fontWeight = FontWeight.Bold)
        TextField(
            value = TextFieldValue(uiState.bio),
            onValueChange = {},
        )
        Text(text = "Profile picture", fontWeight = FontWeight.Bold)
        TextField(
            value = TextFieldValue(uiState.pictureUrl),
            onValueChange = {},
        )
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Update profile")
        }
    }
}
