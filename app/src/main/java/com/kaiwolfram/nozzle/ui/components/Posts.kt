package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.ui.theme.sizing
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun PostCardList(
    posts: List<PostWithMeta>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenProfile: ((String) -> Unit)? = null,
    onNavigateToThread: (String) -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh,
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(posts) { post ->
                PostCard(
                    post = post,
                    onOpenProfile = onOpenProfile,
                    onNavigateToThread = onNavigateToThread,
                )
            }
        }
    }
}

@Composable
fun PostCard(
    post: PostWithMeta,
    modifier: Modifier = Modifier,
    onOpenProfile: ((String) -> Unit)? = null,
    onNavigateToThread: ((String) -> Unit)? = null,
) {
    Row(
        modifier
            .clickable(enabled = onNavigateToThread != null) {
                if (onNavigateToThread != null) {
                    onNavigateToThread(post.id)
                }
            }
            .padding(all = spacing.large)
            .padding(end = spacing.medium)
            .fillMaxWidth()
    ) {
        ProfilePictureIcon(
            modifier = Modifier
                .size(sizing.profilePicture)
                .clip(CircleShape),
            profilePicture = post.picture,
            onOpenProfile = if (onOpenProfile != null) {
                { onOpenProfile(post.pubkey) }
            } else {
                null
            }
        )
        Spacer(Modifier.width(spacing.large))
        Column {
            Text(
                modifier = if (onOpenProfile != null) {
                    Modifier.clickable { onOpenProfile(post.pubkey) }
                } else {
                    Modifier
                },
                text = post.name,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            post.replyToName?.let { ReplyingTo(name = it) }
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
private fun ReplyingTo(name: String) {
    Row {
        Text(
            text = stringResource(id = R.string.replying_to),
            color = Color.LightGray,
        )
        Spacer(modifier = Modifier.width(spacing.medium))
        Text(
            text = name,
            color = Color.LightGray,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun NoPostsHint() {
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
