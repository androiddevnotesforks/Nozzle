package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.theme.sizing

@Composable
fun CopyAndToastIcon(
    toastText: String,
    onCopyAndShowToast: (String) -> Unit,
) {
    CopyIcon(
        modifier = Modifier
            .size(sizing.smallIcon)
            .clickable { onCopyAndShowToast(toastText) },
        description = stringResource(id = R.string.copy_content),
    )
}

@Composable
fun CopyIcon(
    modifier: Modifier = Modifier,
    description: String = stringResource(id = R.string.copy_content),
) {
    Icon(
        modifier = modifier,
        imageVector = Icons.Default.ContentCopy,
        contentDescription = description,
    )
}

@Composable
fun SearchIcon(
    modifier: Modifier = Modifier,
    description: String? = null,
    tint: Color = colors.onBackground,
) {
    Icon(
        modifier = modifier,
        imageVector = Icons.Rounded.Search,
        contentDescription = description,
        tint = tint
    )
}

@Composable
fun VisibilityIcon(isVisible: Boolean, onToggle: () -> Unit) {
    Icon(
        modifier = Modifier
            .size(sizing.smallIcon)
            .clickable { onToggle() },
        imageVector = if (isVisible) {
            Icons.Default.VisibilityOff
        } else {
            Icons.Default.Visibility
        },
        contentDescription = stringResource(id = R.string.toggle_visibility),
    )
}

@Composable
fun ClearIcon(
    modifier: Modifier = Modifier,
    description: String = stringResource(id = R.string.remove),
) {
    Icon(
        modifier = modifier,
        imageVector = Icons.Default.Clear,
        contentDescription = description,
    )
}
