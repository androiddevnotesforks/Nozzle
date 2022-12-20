package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Chat
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
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
    Icon(
        modifier = modifier,
        imageVector = Icons.Default.ContentCopy,
        contentDescription = description,
        tint = tint,
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
fun ReplyIcon(
    modifier: Modifier = Modifier,
    description: String? = stringResource(id = R.string.reply),
    tint: Color = colors.onBackground,
) {
    Icon(
        modifier = modifier,
        imageVector = Icons.Outlined.Chat,
        contentDescription = description,
        tint = tint
    )
}

@Composable
fun RepostIcon(
    modifier: Modifier = Modifier,
    description: String? = stringResource(id = R.string.repost),
    tint: Color = colors.onBackground,
) {
    Icon(
        modifier = modifier,
        imageVector = Icons.Default.Repeat,
        contentDescription = description,
        tint = tint
    )
}

@Composable
fun LikeIcon(
    modifier: Modifier = Modifier,
    isLiked: Boolean = false,
    description: String? = stringResource(id = R.string.like),
    tint: Color = colors.onBackground,
) {
    Icon(
        modifier = modifier,
        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
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
