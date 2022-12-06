package com.kaiwolfram.nozzle.ui.app.views.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.data.room.entity.EventEntity
import com.kaiwolfram.nozzle.ui.components.CopyIcon
import com.kaiwolfram.nozzle.ui.components.ProfilePicture
import com.kaiwolfram.nozzle.ui.components.SearchIcon
import com.kaiwolfram.nozzle.ui.theme.sizing
import com.kaiwolfram.nozzle.ui.theme.spacing


@Composable
fun ProfileScreen(
    uiState: ProfileViewModelState,
    onRefreshProfileView: () -> Unit,
    onCopyPubkeyAndShowToast: (String) -> Unit,
) {
    Column {
        ProfileData(
            pubkey = uiState.pubkey,
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
        PostsOfProfile(
            posts = uiState.posts,
            name = uiState.name,
            picture = uiState.picture,
            isRefreshing = uiState.isRefreshing,
            onRefreshProfileView = onRefreshProfileView,
        )
    }
    if (uiState.posts.isEmpty()) {
        NoPostsHint()
    }
}

@Composable
private fun PostsOfProfile(
    posts: List<EventEntity>,
    name: String,
    picture: Painter,
    isRefreshing: Boolean,
    onRefreshProfileView: () -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefreshProfileView,
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(posts) { post ->
                PostCard(post, name, picture)
            }
        }
    }
}

@Composable
private fun PostCard(
    post: EventEntity,
    name: String,
    picture: Painter,
) {
    Row(
        Modifier
            .padding(all = spacing.large)
            .padding(end = spacing.medium)
            .fillMaxWidth()
    ) {
        ProfilePicture(
            modifier = Modifier
                .size(sizing.profilePic)
                .clip(CircleShape),
            profilePicture = picture
        )
        Spacer(Modifier.width(spacing.large))
        Column {
            Text(
                text = name,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(spacing.medium))
            Text(
                text = post.content,
                maxLines = 12,
                overflow = TextOverflow.Ellipsis
            )
        }
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
        ProfilePicture(
            modifier = Modifier
                .size(sizing.largeProfilePic)
                .aspectRatio(1f)
                .clip(CircleShape),
            profilePicture = picture
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
    val toast = stringResource(id = R.string.copied_pubkey)
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
        )
    }
}

@Composable
private fun NoPostsHint() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        SearchIcon(modifier = Modifier.fillMaxSize(0.1f), tint = Color.LightGray)
        Text(
            text = stringResource(id = R.string.no_posts_found),
            textAlign = TextAlign.Center,
            color = Color.LightGray
        )
    }
}
