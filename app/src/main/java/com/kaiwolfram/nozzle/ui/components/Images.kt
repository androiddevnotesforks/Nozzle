package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.data.utils.getRobohashUrl

@Composable
fun ProfilePicture(
    pictureUrl: String,
    pubkey: String,
    modifier: Modifier = Modifier,
    onOpenProfile: (() -> Unit)? = null,
) {
    val firstIsError = remember { mutableStateOf(false) }
    if (!firstIsError.value) {
        BaseProfilePicture(
            modifier = modifier,
            pictureUrl = pictureUrl,
            onError = { firstIsError.value = true },
            onOpenProfile = onOpenProfile
        )
    } else {
        BaseProfilePicture(
            modifier = modifier,
            pictureUrl = getRobohashUrl(pubkey),
            onOpenProfile = onOpenProfile
        )
    }
}

@Composable
fun BaseProfilePicture(
    pictureUrl: String,
    modifier: Modifier = Modifier,
    onError: (() -> Unit)? = null,
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
        onError = {
            if (onError != null) {
                onError()
            }
        },
        error = painterResource(R.drawable.ic_default_profile),
        fallback = painterResource(R.drawable.ic_default_profile),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(R.drawable.ic_default_profile),
        contentDescription = stringResource(id = R.string.profile_picture)
    )
}
