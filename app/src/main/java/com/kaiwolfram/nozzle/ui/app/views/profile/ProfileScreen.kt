package com.kaiwolfram.nozzle.ui.app.views.profile

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.data.room.entity.EventEntity
import com.kaiwolfram.nozzle.ui.components.ProfilePicture


@Composable
fun ProfileScreen(
    uiState: ProfileViewModelState,
    onRefreshProfileView: () -> Unit,
    onCopyPubkeyAndShowToast: (Context, ClipboardManager, String) -> Unit,
) {
    Column {
        ProfileData(
            pubkey = uiState.pubkey,
            name = uiState.name,
            bio = uiState.bio,
            picture = uiState.picture,
            onCopyPubkeyAndShowToast = onCopyPubkeyAndShowToast,
        )
        Spacer(Modifier.height(4.dp))
        FollowerNumbers(
            numOfFollowing = uiState.numOfFollowing,
            numOfFollowers = uiState.numOfFollowers,
        )
        Spacer(Modifier.height(12.dp))
        Divider()
        Posts(
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
private fun Posts(
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
            .padding(all = 8.dp)
            .padding(end = 4.dp)
            .fillMaxWidth()
    ) {
        ProfilePicture(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            profilePicture = picture
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = name,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
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
    onCopyPubkeyAndShowToast: (Context, ClipboardManager, String) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .padding(end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfilePicture(
            modifier = Modifier
                .size(60.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
            profilePicture = picture
        )
        Spacer(Modifier.width(4.dp))
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
            .padding(horizontal = 12.dp),
    ) {
        Row {
            Text(
                text = numOfFollowing.toString(),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(4.dp))
            Text(text = stringResource(id = R.string.following))
        }
        Spacer(Modifier.width(8.dp))
        Row {
            Text(
                text = numOfFollowers.toString(),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = stringResource(id = R.string.followers))
        }
    }
}

@Composable
private fun NameAndPubkey(
    name: String,
    pubkey: String,
    onCopyPubkeyAndShowToast: (Context, ClipboardManager, String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            Modifier
                .padding(end = 4.dp)
                .weight(weight = 3.3f)
        ) {
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6,
            )
            CopyablePubkey(pubkey = pubkey, onCopyPubkeyAndShowToast = onCopyPubkeyAndShowToast)
        }
    }
}

@Composable
private fun CopyablePubkey(
    pubkey: String,
    onCopyPubkeyAndShowToast: (Context, ClipboardManager, String) -> Unit,
) {
    val toast = stringResource(id = R.string.copied_pubkey)
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    Row(
        Modifier.clickable {
            onCopyPubkeyAndShowToast(
                context,
                clipboardManager,
                toast
            )
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = pubkey,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.LightGray,
            style = MaterialTheme.typography.body2,
        )
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Icons.Rounded.ContentCopy,
            contentDescription = stringResource(id = R.string.copy_pubkey),
            tint = Color.LightGray
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
        Icon(
            modifier = Modifier.fillMaxSize(0.1f),
            contentDescription = null,
            imageVector = Icons.Rounded.Search,
            tint = Color.LightGray
        )
        Text(
            text = stringResource(id = R.string.no_posts_found),
            textAlign = TextAlign.Center,
            color = Color.LightGray
        )
    }
}
