package com.kaiwolfram.nozzle.ui.app.views.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.PostIds
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.ui.components.AddIcon
import com.kaiwolfram.nozzle.ui.components.NoPostsHint
import com.kaiwolfram.nozzle.ui.components.PostCardList
import com.kaiwolfram.nozzle.ui.components.ProfilePicture
import com.kaiwolfram.nozzle.ui.theme.White21
import com.kaiwolfram.nozzle.ui.theme.sizing
import com.kaiwolfram.nozzle.ui.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(
    uiState: FeedViewModelState,
    metadataState: Metadata?,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onRefreshFeedView: () -> Unit,
    onPrepareReply: (PostWithMeta) -> Unit,
    onPreparePost: () -> Unit,
    onLoadMore: () -> Unit,
    onOpenDrawer: () -> Unit,
    onNavigateToThread: (PostIds) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToReply: () -> Unit,
    onNavigateToPost: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            FeedTopBar(
                picture = metadataState?.picture.orEmpty(),
                pubkey = uiState.pubkey,
                onPictureClick = onOpenDrawer,
                onScrollToTop = { scope.launch { lazyListState.animateScrollToItem(0) } }
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
                lazyListState = lazyListState,
                onLike = onLike,
                onRepost = onRepost,
                onPrepareReply = onPrepareReply,
                onLoadMore = onLoadMore,
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
    picture: String,
    pubkey: String,
    onPictureClick: () -> Unit,
    onScrollToTop: () -> Unit
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
                    pictureUrl = picture,
                    pubkey = pubkey,
                    modifier = Modifier
                        .size(sizing.smallProfilePicture)
                        .clip(CircleShape)
                        .drawBehind { drawCircle(color = White21, radius = size.width / 2) }
                        .clickable { onPictureClick() },
                )
            }
            Text(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onScrollToTop() },
                text = stringResource(id = R.string.app_name),
                style = typography.h6,
                color = colors.background
            )
            Row {
                Spacer(modifier = Modifier.size(sizing.smallProfilePicture))
                Spacer(modifier = Modifier.width(spacing.large))
            }
        }
    }
}

@Composable
private fun FeedFab(onPrepareNewPost: () -> Unit) {
    FloatingActionButton(onClick = { onPrepareNewPost() }, contentColor = colors.surface) {
        AddIcon()
    }
}
