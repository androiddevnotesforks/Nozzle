package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kaiwolfram.nozzle.R

@Composable
fun ProfilePicture(
    pictureUrl: String,
    modifier: Modifier = Modifier,
    onOpenProfile: (() -> Unit)? = null,
) {
    AsyncImage(
        modifier = if (onOpenProfile != null)
            modifier
                .clip(CircleShape)
                .clickable { onOpenProfile() }
        else {
            modifier.clip(CircleShape)
        },
        model = ImageRequest.Builder(LocalContext.current)
            .data(pictureUrl)
            .crossfade(true)
            .size(300)
            .build(),
        error = painterResource(R.drawable.ic_default_profile),
        placeholder = painterResource(R.drawable.ic_default_profile),
        contentDescription = stringResource(id = R.string.profile_picture)
    )
}
