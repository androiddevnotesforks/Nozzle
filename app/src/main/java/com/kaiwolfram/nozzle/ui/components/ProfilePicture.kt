package com.kaiwolfram.nozzle.ui.components

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nozzle.R

@Composable
fun ProfilePicture(
    profilePicture: Painter,
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier,
        painter = profilePicture,
        contentDescription = stringResource(id = R.string.profile_picture),
        tint = Color.Unspecified,
    )
}
