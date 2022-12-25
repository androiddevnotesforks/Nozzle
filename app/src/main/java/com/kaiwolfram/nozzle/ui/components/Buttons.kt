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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
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
fun SendButton(
    isSendable: Boolean,
    onSend: () -> Unit,
    onGoBack: () -> Unit,
) {
    TopBarButton(
        imageVector = Icons.Default.Send,
        hasChanges = isSendable,
        description = stringResource(id = R.string.send),
        onClick = onSend,
        onCanGoBack = { true },
        onGoBack = onGoBack,
    )
}

@Composable
fun CheckButton(
    hasChanges: Boolean,
    onCheck: () -> Unit,
    onCanGoBack: (() -> Boolean)? = null,
    onGoBack: (() -> Unit)? = null,
) {
    TopBarButton(
        imageVector = Icons.Default.Check,
        hasChanges = hasChanges,
        description = stringResource(id = R.string.update),
        onClick = onCheck,
        onCanGoBack = onCanGoBack,
        onGoBack = onGoBack,
    )
}

@Composable
private fun TopBarButton(
    imageVector: ImageVector,
    hasChanges: Boolean,
    description: String,
    onClick: () -> Unit,
    onCanGoBack: (() -> Boolean)? = null,
    onGoBack: (() -> Unit)? = null,
) {
    Icon(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                if (hasChanges) {
                    onClick()
                    if (onCanGoBack != null && onGoBack != null) {
                        if (onCanGoBack()) onGoBack()
                    }
                }
            },
        imageVector = imageVector,
        contentDescription = description,
        tint = if (hasChanges) colors.surface else LightGray21
    )
}

@Composable
fun EditProfileButton(onNavToEditProfile: () -> Unit) {
    Button(
        onClick = { onNavToEditProfile() },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colors.onBackground),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colors.onBackground,
            backgroundColor = colors.background
        )
    ) {
        Text(text = stringResource(id = R.string.edit))
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
