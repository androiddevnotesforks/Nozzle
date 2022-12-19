package com.kaiwolfram.nozzle.ui.app.views.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.CopyIcon
import com.kaiwolfram.nozzle.ui.components.NoPostsHint
import com.kaiwolfram.nozzle.ui.components.PostCardList
import com.kaiwolfram.nozzle.ui.components.ProfilePictureIcon
import com.kaiwolfram.nozzle.ui.theme.sizing
import com.kaiwolfram.nozzle.ui.theme.spacing


@Composable
fun ProfileScreen(
    uiState: ProfileViewModelState,
    onRefreshProfileView: () -> Unit,
    onCopyPubkeyAndShowToast: (String) -> Unit,
    onNavigateToThread: (String) -> Unit
) {
    Column {
        ProfileData(
            pubkey = uiState.shortenedPubkey,
            name = uiState.name,
            bio = uiState.bio,
            picture = uiState.picture,
            onCopyPubkeyAndShowToast = onCopyPubkeyAndShowToast,
        )
        Spacer(Modifier.height(spacing.medium))
        FollowerNumbers(
            numOfFollowing = uiState.numOfFollowing,
            numOfFollowers = uiState.numOfFollowers,
        )
        Spacer(Modifier.height(spacing.xl))
        Divider()
        PostCardList(
            posts = uiState.posts,
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefreshProfileView,
            onNavigateToThread = onNavigateToThread
        )
    }
    if (uiState.posts.isEmpty()) {
        NoPostsHint()
    }
}

@Composable
private fun ProfileData(
    pubkey: String,
    name: String,
    bio: String,
    picture: Painter,
    onCopyPubkeyAndShowToast: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(spacing.large)
            .padding(end = spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfilePictureIcon(
            modifier = Modifier
                .size(sizing.largeProfilePicture)
                .aspectRatio(1f)
                .clip(CircleShape),
            profilePicture = picture,
        )
        Spacer(Modifier.width(spacing.medium))
        Column(verticalArrangement = Arrangement.Center) {
            NameAndPubkey(
                name = name,
                pubkey = pubkey,
                onCopyPubkeyAndShowToast = onCopyPubkeyAndShowToast,
            )
            if (bio.isNotBlank()) {
                Text(
                    text = bio,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FollowerNumbers(
    numOfFollowing: Int,
    numOfFollowers: Int,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.xl),
    ) {
        Row {
            Text(
                text = numOfFollowing.toString(),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(spacing.medium))
            Text(text = stringResource(id = R.string.following))
        }
        Spacer(Modifier.width(spacing.large))
        Row {
            Text(
                text = numOfFollowers.toString(),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(spacing.medium))
            Text(text = stringResource(id = R.string.followers))
        }
    }
}

@Composable
private fun NameAndPubkey(
    name: String,
    pubkey: String,
    onCopyPubkeyAndShowToast: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(Modifier.padding(end = spacing.medium)) {
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6,
            )
            CopyablePubkey(
                pubkey = pubkey,
                onCopyPubkeyAndShowToast = onCopyPubkeyAndShowToast
            )
        }
    }
}

@Composable
private fun CopyablePubkey(
    pubkey: String,
    onCopyPubkeyAndShowToast: (String) -> Unit,
) {
    val toast = stringResource(id = R.string.pubkey_copied)
    Row(
        Modifier.clickable { onCopyPubkeyAndShowToast(toast) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = pubkey,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.LightGray,
            style = MaterialTheme.typography.body2,
        )
        CopyIcon(
            modifier = Modifier.size(sizing.smallIcon),
            description = stringResource(id = R.string.copy_pubkey),
            tint = Color.LightGray
        )
    }
}
