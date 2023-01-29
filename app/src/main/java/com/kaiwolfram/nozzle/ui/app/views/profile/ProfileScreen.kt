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
import androidx.compose.ui.text.style.TextOverflow
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.PostIds
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.model.ProfileWithAdditionalInfo
import com.kaiwolfram.nozzle.ui.components.CopyIcon
import com.kaiwolfram.nozzle.ui.components.EditProfileButton
import com.kaiwolfram.nozzle.ui.components.FollowButton
import com.kaiwolfram.nozzle.ui.components.ProfilePicture
import com.kaiwolfram.nozzle.ui.components.postCard.NoPostsHint
import com.kaiwolfram.nozzle.ui.components.postCard.PostCardList
import com.kaiwolfram.nozzle.ui.components.text.NumberedCategory
import com.kaiwolfram.nozzle.ui.theme.LightGray21
import com.kaiwolfram.nozzle.ui.theme.sizing
import com.kaiwolfram.nozzle.ui.theme.spacing


@Composable
fun ProfileScreen(
    isRefreshing: Boolean,
    profile: ProfileWithAdditionalInfo,
    posts: List<PostWithMeta>,
    onPrepareReply: (PostWithMeta) -> Unit,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onFollow: (String) -> Unit,
    onUnfollow: (String) -> Unit,
    onRefreshProfileView: () -> Unit,
    onCopyNpub: () -> Unit,
    onLoadMore: () -> Unit,
    onNavigateToThread: (PostIds) -> Unit,
    onNavigateToReply: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
) {
    Column {
        ProfileData(
            profile = profile,
            onFollow = onFollow,
            onUnfollow = onUnfollow,
            onCopyNpub = onCopyNpub,
            onNavToEditProfile = onNavigateToEditProfile,
        )
        Spacer(Modifier.height(spacing.medium))
        NumberedCategories(
            numOfFollowing = profile.numOfFollowing,
            numOfFollowers = profile.numOfFollowers,
            numOfRelays = profile.numOfRelays,
        )
        Spacer(Modifier.height(spacing.xl))
        Divider()
        PostCardList(
            posts = posts,
            isRefreshing = isRefreshing,
            onRefresh = onRefreshProfileView,
            onLike = onLike,
            onRepost = onRepost,
            onPrepareReply = onPrepareReply,
            onLoadMore = onLoadMore,
            onNavigateToThread = onNavigateToThread,
            onNavigateToReply = onNavigateToReply
        )
    }
    if (posts.isEmpty()) {
        NoPostsHint()
    }
}

@Composable
private fun ProfileData(
    profile: ProfileWithAdditionalInfo,
    onFollow: (String) -> Unit,
    onUnfollow: (String) -> Unit,
    onCopyNpub: () -> Unit,
    onNavToEditProfile: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = spacing.screenEdge),
        verticalArrangement = Arrangement.Center
    ) {
        ProfilePictureAndActions(
            pictureUrl = profile.metadata.picture.orEmpty(),
            pubkey = profile.pubkey,
            isOneself = profile.isOneself,
            isFollowed = profile.isFollowedByMe,
            onFollow = onFollow,
            onUnfollow = onUnfollow,
            onNavToEditProfile = onNavToEditProfile,
        )
        NameAndNpub(
            name = profile.metadata.name.orEmpty(),
            npub = profile.npub,
            onCopyNpub = onCopyNpub,
        )
        Spacer(Modifier.height(spacing.medium))
        if (profile.metadata.about.orEmpty().isNotBlank()) {
            Text(
                text = profile.metadata.about.orEmpty(),
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
    isOneself: Boolean,
    isFollowed: Boolean,
    onFollow: (String) -> Unit,
    onUnfollow: (String) -> Unit,
    onNavToEditProfile: () -> Unit,
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
        FollowOrEditButton(
            isOneself = isOneself,
            isFollowed = isFollowed,
            onFollow = { onFollow(pubkey) },
            onUnfollow = { onUnfollow(pubkey) },
            onNavToEditProfile = onNavToEditProfile,
        )
    }
}

@Composable
private fun FollowOrEditButton(
    isOneself: Boolean,
    isFollowed: Boolean,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit,
    onNavToEditProfile: () -> Unit,
) {
    if (isOneself) {
        EditProfileButton(onNavToEditProfile = onNavToEditProfile)
    } else {
        FollowButton(
            isFollowed = isFollowed,
            onFollow = { onFollow() },
            onUnfollow = { onUnfollow() }
        )
    }
}

@Composable
private fun NumberedCategories(
    numOfFollowing: Int,
    numOfFollowers: Int,
    numOfRelays: Int,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.xl),
    ) {
        Row {
            NumberedCategory(
                number = numOfFollowing,
                category = stringResource(id = R.string.following)
            )
            Spacer(Modifier.width(spacing.large))
            NumberedCategory(
                number = numOfFollowers,
                category = stringResource(id = R.string.followers)
            )
            Spacer(Modifier.width(spacing.large))
            NumberedCategory(
                number = numOfRelays,
                category = stringResource(id = R.string.relays)
            )
        }
    }
}

@Composable
private fun NameAndNpub(
    name: String,
    npub: String,
    onCopyNpub: () -> Unit,
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
            CopyableNpub(
                npub = npub,
                onCopyNpub = onCopyNpub
            )
        }
    }
}

@Composable
private fun CopyableNpub(
    npub: String,
    onCopyNpub: () -> Unit,
) {
    Row(
        Modifier.clickable { onCopyNpub() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CopyIcon(
            modifier = Modifier.size(sizing.smallIcon),
            description = stringResource(id = R.string.copy_pubkey),
            tint = LightGray21
        )
        Text(
            text = npub,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = LightGray21,
            style = MaterialTheme.typography.body2,
        )
    }
}
