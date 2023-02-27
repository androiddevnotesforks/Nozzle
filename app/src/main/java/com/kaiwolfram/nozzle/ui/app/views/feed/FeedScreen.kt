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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nostrclientkt.model.RelaySelection
import com.kaiwolfram.nozzle.model.FeedSettings
import com.kaiwolfram.nozzle.model.PostIds
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.model.RelayActive
import com.kaiwolfram.nozzle.ui.components.AddIcon
import com.kaiwolfram.nozzle.ui.components.ChooseRelayButton
import com.kaiwolfram.nozzle.ui.components.FeedSettingsButton
import com.kaiwolfram.nozzle.ui.components.ProfilePicture
import com.kaiwolfram.nozzle.ui.components.postCard.NoPostsHint
import com.kaiwolfram.nozzle.ui.components.postCard.PostCardList
import com.kaiwolfram.nozzle.ui.theme.White21
import com.kaiwolfram.nozzle.ui.theme.sizing
import com.kaiwolfram.nozzle.ui.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(
    uiState: FeedViewModelState,
    feedState: List<PostWithMeta>,
    metadataState: Metadata?,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onRefreshFeedView: () -> Unit,
    onRefreshOnMenuDismiss: () -> Unit,
    onPrepareReply: (PostWithMeta) -> Unit,
    onPreparePost: (RelaySelection) -> Unit,
    onToggleContactsOnly: () -> Unit,
    onTogglePosts: () -> Unit,
    onToggleReplies: () -> Unit,
    onToggleRelayIndex: (Int) -> Unit,
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
                feedSettings = uiState.feedSettings,
                relayStatuses = uiState.relayStatuses,
                onRefreshOnMenuDismiss = onRefreshOnMenuDismiss,
                onToggleContactsOnly = onToggleContactsOnly,
                onTogglePosts = onTogglePosts,
                onToggleReplies = onToggleReplies,
                onPictureClick = onOpenDrawer,
                onToggleRelayIndex = onToggleRelayIndex,
                onScrollToTop = { scope.launch { lazyListState.animateScrollToItem(0) } })
        },
        floatingActionButton = {
            FeedFab(onPrepareNewPost = {
                onPreparePost(uiState.feedSettings.relaySelection)
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
                posts = feedState,
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
        if (feedState.isEmpty()) {
            NoPostsHint()
        }
    }
}

@Composable
private fun FeedTopBar(
    picture: String,
    pubkey: String,
    feedSettings: FeedSettings,
    relayStatuses: List<RelayActive>,
    onRefreshOnMenuDismiss: () -> Unit,
    onToggleContactsOnly: () -> Unit,
    onTogglePosts: () -> Unit,
    onToggleReplies: () -> Unit,
    onPictureClick: () -> Unit,
    onToggleRelayIndex: (Int) -> Unit,
    onScrollToTop: () -> Unit
) {
    TopAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(0.15f)) {
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
            Headline(
                modifier = Modifier.weight(0.7f),
                headline = stringResource(id = com.kaiwolfram.nozzle.R.string.home),
                onScrollToTop = onScrollToTop,
            )
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.weight(0.15f)) {
                ChooseRelayButton(relays = relayStatuses, onClickIndex = onToggleRelayIndex)
                Spacer(modifier = Modifier.width(spacing.large))
                FeedSettingsButton(
                    feedSettings = feedSettings,
                    onRefreshOnMenuDismiss = onRefreshOnMenuDismiss,
                    onToggleContactsOnly = onToggleContactsOnly,
                    onTogglePosts = onTogglePosts,
                    onToggleReplies = onToggleReplies,
                )
                Spacer(modifier = Modifier.width(spacing.medium))
            }
        }
    }
}

@Composable
private fun Headline(
    headline: String,
    onScrollToTop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onScrollToTop() },
            text = headline.removePrefix("wss://"),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = typography.h6,
            color = colors.background
        )
    }
}

@Composable
private fun FeedFab(onPrepareNewPost: () -> Unit) {
    FloatingActionButton(onClick = { onPrepareNewPost() }, contentColor = colors.surface) {
        AddIcon()
    }
}
