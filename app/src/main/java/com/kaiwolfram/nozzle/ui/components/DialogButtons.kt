package com.kaiwolfram.nozzle.ui.components

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nozzle.R

@Composable
fun DialogConfirmButton(
    value: String,
    onChangeValue: (String) -> Unit,
    onCloseDialog: () -> Unit
) {
    TextButton(onClick = {
        onChangeValue(value)
        onCloseDialog()
    }) {
        Text(text = stringResource(id = R.string.confirm))
    }
}

@Composable
fun DialogDismissButton(onCloseDialog: () -> Unit) {
    TextButton(onClick = onCloseDialog) {
        Text(text = stringResource(id = R.string.dismiss))
    }
}
