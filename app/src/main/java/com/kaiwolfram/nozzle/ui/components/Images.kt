package com.kaiwolfram.nozzle.ui.components

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
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
    val isError = remember { mutableStateOf(false) }
    val url = remember { mutableStateOf(pictureUrl) }
    if (pictureUrl != url.value) {
        url.value = pictureUrl
        isError.value = false
    }
    if (!isError.value) {
        BaseProfilePicture(
            modifier = modifier,
            pictureUrl = pictureUrl.ifEmpty { getRobohashUrl(pubkey) },
            onError = { isError.value = true },
            onOpenProfile = onOpenProfile
        )
    } else {
        Image(
            modifier = modifier.clip(CircleShape),
            painter = painterResource(id = R.drawable.ic_default_profile),
            contentDescription = stringResource(id = R.string.profile_picture)
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
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context).components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()
    }
    Image(
        modifier = if (onOpenProfile != null)
            modifier
                .clip(CircleShape)
                .clickable { onOpenProfile() }
        else {
            modifier.clip(CircleShape)
        },
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(pictureUrl)
                .crossfade(true)
                .size(300)
                .build(),
            imageLoader = imageLoader,
            onError = {
                if (onError != null) {
                    onError()
                }
            }
        ),
        contentScale = ContentScale.Crop,
        contentDescription = stringResource(id = R.string.profile_picture)
    )
}
