package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.*

@Composable
fun ChangeableTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    maxLines: Int = 1,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorLabel: String? = null,
    placeholder: String? = null,
    isPassword: Boolean = false,
    onChangeValue: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    var newValue by remember { mutableStateOf(TextFieldValue(value)) }
    TextField(
        modifier = modifier,
        value = newValue,
        isError = isError,
        maxLines = maxLines,
        placeholder = if (placeholder != null) {
            { Text(text = placeholder) }
        } else {
            null
        },
        label = if (isError && errorLabel != null) {
            { Text(text = errorLabel) }
        } else {
            null
        },
        onValueChange = { change ->
            newValue = change
            onChangeValue(newValue.text)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done,
            autoCorrect = false,
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = trailingIcon,
    )
}