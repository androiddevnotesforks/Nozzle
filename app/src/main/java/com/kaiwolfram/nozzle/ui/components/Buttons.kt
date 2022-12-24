package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.theme.LightGray21

@Composable
fun GoBackButton(onGoBack: () -> Unit) {
    Icon(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onGoBack() },
        imageVector = Icons.Default.ArrowBack,
        contentDescription = stringResource(id = R.string.return_back),
    )
}

@Composable
fun CloseButton(onGoBack: () -> Unit) {
    Icon(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onGoBack() },
        imageVector = Icons.Default.Close,
        contentDescription = stringResource(id = R.string.close),
    )
}

@Composable
fun SendButton(enabled: Boolean, onSend: () -> Unit, onGoBack: () -> Unit) {
    Button(
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colors.onBackground,
            backgroundColor = colors.background,
            disabledContentColor = LightGray21
        ),
        onClick = {
            onSend()
            onGoBack()
        },
    ) {
        Text(text = stringResource(id = R.string.send))
    }
}

@Composable
fun FollowButton(
    isFollowed: Boolean,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit
) {
    if (isFollowed) {
        Button(
            onClick = { onUnfollow() },
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colors.onBackground),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colors.onBackground,
                backgroundColor = colors.background
            )
        ) {
            Text(text = stringResource(id = R.string.following))
        }
    } else {
        Button(
            onClick = { onFollow() },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colors.background,
                backgroundColor = colors.onBackground
            )
        ) {
            Text(text = stringResource(id = R.string.follow))
        }
    }
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
