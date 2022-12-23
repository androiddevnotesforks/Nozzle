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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.*
import com.kaiwolfram.nozzle.ui.theme.LightGray21
import com.kaiwolfram.nozzle.ui.theme.sizing
import com.kaiwolfram.nozzle.ui.theme.spacing


@Composable
fun ProfileScreen(
    uiState: ProfileViewModelState,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onFollow: (String) -> Unit,
    onUnfollow: (String) -> Unit,
    onRefreshProfileView: () -> Unit,
    onCopyPubkeyAndShowToast: (String) -> Unit,
    onNavigateToThread: (String) -> Unit,
    onNavigateToReply: () -> Unit,
) {
    Column {
        ProfileData(
            pubkey = uiState.pubkey,
            name = uiState.name,
            bio = uiState.bio,
            pictureUrl = uiState.pictureUrl,
            isFollowed = uiState.isFollowed,
            onFollow = onFollow,
            onUnfollow = onUnfollow,
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
            onLike = onLike,
            onRepost = onRepost,
            onNavigateToThread = onNavigateToThread,
            onNavigateToReply = onNavigateToReply
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
    pictureUrl: String,
    isFollowed: Boolean,
    onFollow: (String) -> Unit,
    onUnfollow: (String) -> Unit,
    onCopyPubkeyAndShowToast: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = spacing.screenEdge),
        verticalArrangement = Arrangement.Center
    ) {
        ProfilePictureAndActions(
            pictureUrl = pictureUrl,
            pubkey = pubkey,
            isFollowed = isFollowed,
            onFollow = onFollow,
            onUnfollow = onUnfollow,
        )
        NameAndPubkey(
            name = name,
            pubkey = pubkey,
            onCopyPubkeyAndShowToast = onCopyPubkeyAndShowToast,
        )
        Spacer(Modifier.height(spacing.medium))
        if (bio.isNotBlank()) {
            Text(
                text = bio,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProfilePictureAndActions(
    pictureUrl: String,
    pubkey: String,
    isFollowed: Boolean,
    onFollow: (String) -> Unit,
    onUnfollow: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(spacing.screenEdge)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProfilePicture(
            modifier = Modifier
                .size(sizing.largeProfilePicture)
                .aspectRatio(1f)
                .clip(CircleShape),
            pictureUrl = pictureUrl,
            pubkey = pubkey,
        )
        FollowButton(
            isFollowed = isFollowed,
            onFollow = { onFollow(pubkey) },
            onUnfollow = { onUnfollow(pubkey) }
        )

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
        CopyIcon(
            modifier = Modifier.size(sizing.smallIcon),
            description = stringResource(id = R.string.copy_pubkey),
            tint = LightGray21
        )
        Text(
            text = pubkey,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = LightGray21,
            style = MaterialTheme.typography.body2,
        )
    }
}
