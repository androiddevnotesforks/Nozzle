package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nozzle.R

@Composable
fun GoBackButton(onGoBack: () -> Unit) {
    Icon(
        modifier = Modifier
            .clickable { onGoBack() }
            .clip(CircleShape),
        imageVector = Icons.Default.ArrowBack,
        contentDescription = stringResource(id = R.string.return_back),
    )
}

@Composable
fun ActionButton(
    text: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    clearFocusAfterAction: Boolean = false,
) {
    val focusManager = LocalFocusManager.current
    Button(
        modifier = modifier,
        onClick = {
            onAction()
            if (clearFocusAfterAction) {
                focusManager.clearFocus()
            }
        },
    ) {
        Text(text = text)
    }
}
