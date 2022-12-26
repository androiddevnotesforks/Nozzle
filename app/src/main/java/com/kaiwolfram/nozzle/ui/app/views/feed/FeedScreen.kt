package com.kaiwolfram.nozzle.ui.app.views.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.ui.components.AddIcon
import com.kaiwolfram.nozzle.ui.components.NoPostsHint
import com.kaiwolfram.nozzle.ui.components.PostCardList
import com.kaiwolfram.nozzle.ui.components.ProfilePicture
import com.kaiwolfram.nozzle.ui.theme.White21
import com.kaiwolfram.nozzle.ui.theme.sizing
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun FeedScreen(
    uiState: FeedViewModelState,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onRefreshFeedView: () -> Unit,
    onPrepareReply: (PostWithMeta) -> Unit,
    onPreparePost: () -> Unit,
    onOpenDrawer: () -> Unit,
    onNavigateToThread: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToReply: () -> Unit,
    onNavigateToPost: () -> Unit,
) {
    Scaffold(
        topBar = {
            FeedTopBar(
                pictureUrl = uiState.pictureUrl,
                pubkey = uiState.pubkey,
                onPictureClick = onOpenDrawer,
            )
        },
        floatingActionButton = {
            FeedFab(onPrepareNewPost = {
                onPreparePost()
                onNavigateToPost()
            })
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            PostCardList(
                posts = uiState.posts,
                isRefreshing = uiState.isRefreshing,
                onLike = onLike,
                onRepost = onRepost,
                onPrepareReply = onPrepareReply,
                onRefresh = onRefreshFeedView,
                onOpenProfile = onNavigateToProfile,
                onNavigateToThread = onNavigateToThread,
                onNavigateToReply = onNavigateToReply,
            )
        }
        if (uiState.posts.isEmpty()) {
            NoPostsHint()
        }
    }
}

@Composable
private fun FeedTopBar(
    pictureUrl: String,
    pubkey: String,
    onPictureClick: () -> Unit
) {
    TopAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Spacer(modifier = Modifier.width(spacing.large))
                ProfilePicture(
                    pictureUrl = pictureUrl,
                    pubkey = pubkey,
                    modifier = Modifier
                        .size(sizing.smallProfilePicture)
                        .clip(CircleShape)
                        .drawBehind { drawCircle(color = White21, radius = size.width / 2) }
                        .clickable { onPictureClick() },
                )
            }
            Text(
                text = stringResource(id = R.string.app_name),
                style = typography.h6,
                color = colors.background
            )
            // TODO: Replace with settings icon
            Row {
                Spacer(modifier = Modifier.size(sizing.smallProfilePicture))
                Spacer(modifier = Modifier.width(spacing.large))
            }
        }
    }
}

@Composable
private fun FeedFab(
    onPrepareNewPost: () -> Unit
) {
    FloatingActionButton(onClick = { onPrepareNewPost() }) {
        AddIcon()
    }
}
