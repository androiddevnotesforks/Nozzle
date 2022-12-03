package com.kaiwolfram.nozzle.ui.app.views.relays

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import com.kaiwolfram.nozzle.R

@Composable
fun RelaysScreen(
    uiState: RelaysViewModelState,
) {
    Column {
        TopAppBar(backgroundColor = colors.background) {
            Text(text = stringResource(id = R.string.relays))
        }
        for(relay in uiState.urls){
            Text(text = "Relay", fontWeight = FontWeight.Bold)
            TextField(
                value = TextFieldValue(relay),
                enabled = false,
                onValueChange = {},
            )
        }
        Text(text = "Add relay", fontWeight = FontWeight.Bold)
        TextField(
            value = TextFieldValue(),
            onValueChange = {},
        )
    }
}
