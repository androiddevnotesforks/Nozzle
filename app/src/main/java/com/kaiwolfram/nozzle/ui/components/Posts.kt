package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.model.ThreadPosition
import com.kaiwolfram.nozzle.ui.theme.DarkGray21
import com.kaiwolfram.nozzle.ui.theme.LightGray21
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
    threadPosition: ThreadPosition = ThreadPosition.SINGLE,
    onOpenProfile: ((String) -> Unit)? = null,
    onNavigateToThread: ((String) -> Unit)? = null,
) {
    val x = sizing.profilePicture / 2 + spacing.screenEdge
    val yTop = spacing.screenEdge
    val yBottom = sizing.profilePicture + spacing.screenEdge
    val small = spacing.small
    Row(
        modifier
            .clickable(enabled = onNavigateToThread != null) {
                if (onNavigateToThread != null) {
                    onNavigateToThread(post.id)
                }
            }
            .fillMaxWidth()
            .drawBehind {
                when (threadPosition) {
                    ThreadPosition.START -> {
                        drawThread(
                            scope = this,
                            x = x.toPx(),
                            yStart = yBottom.toPx(),
                            yEnd = size.height,
                            width = small.toPx()
                        )
                    }
                    ThreadPosition.MIDDLE -> {
                        drawThread(
                            scope = this,
                            x = x.toPx(),
                            yStart = 0f,
                            yEnd = yTop.toPx(),
                            width = small.toPx()
                        )
                        drawThread(
                            scope = this,
                            x = x.toPx(),
                            yStart = yBottom.toPx(),
                            yEnd = size.height,
                            width = small.toPx()
                        )
                    }
                    ThreadPosition.END -> {
                        drawThread(
                            scope = this,
                            x = x.toPx(),
                            yStart = 0f,
                            yEnd = yTop.toPx(),
                            width = small.toPx()
                        )
                    }
                    ThreadPosition.SINGLE -> {}
                }
            }
            .padding(all = spacing.screenEdge)
            .padding(end = spacing.medium)
            .clipToBounds()
    ) {
        ProfilePicture(
            modifier = Modifier
                .size(sizing.profilePicture)
                .clip(CircleShape),
            pictureUrl = post.pictureUrl,
            pubkey = post.pubkey,
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
                maxLines = 21,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(spacing.medium))
            PostCardActions(numOfReplies = 0, numOfReposts = 0, numOfLikes = 0)
        }
    }
}

@Composable
fun PostNotFound() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenEdge)
            .padding(top = spacing.screenEdge)
            .clip(shapes.medium)
            .border(width = spacing.tiny, color = DarkGray21, shape = shapes.medium)
            .background(LightGray21)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.screenEdge),
            text = stringResource(id = R.string.post_not_found),
            textAlign = TextAlign.Center,
            color = DarkGray21
        )
    }
}

@Composable
private fun PostCardActions(
    numOfReplies: Int,
    numOfReposts: Int,
    numOfLikes: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(0.8f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ReplyAction(numOfReplies = numOfReplies)
        RepostAction(numOfReposts = numOfReposts)
        LikeAction(numOfLikes = numOfLikes)
    }
}

@Composable
private fun ReplyAction(
    numOfReplies: Int,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ReplyIcon(modifier = Modifier.size(sizing.smallIcon))
        Spacer(Modifier.width(spacing.medium))
        Text(text = numOfReplies.toString())
    }
}

@Composable
private fun RepostAction(
    numOfReposts: Int,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RepostIcon(modifier = Modifier.size(sizing.smallIcon))
        Spacer(Modifier.width(spacing.medium))
        Text(text = numOfReposts.toString())
    }
}

@Composable
private fun LikeAction(
    numOfLikes: Int,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        LikeIcon(modifier = Modifier.size(sizing.smallIcon))
        Spacer(Modifier.width(spacing.medium))
        Text(text = numOfLikes.toString())
    }
}


@Composable
private fun ReplyingTo(name: String) {
    Row {
        Text(
            text = stringResource(id = R.string.replying_to),
            color = LightGray21,
        )
        Spacer(modifier = Modifier.width(spacing.medium))
        Text(
            text = name,
            color = LightGray21,
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
        SearchIcon(modifier = Modifier.fillMaxSize(0.1f), tint = LightGray21)
        Text(
            text = stringResource(id = R.string.no_posts_found),
            textAlign = TextAlign.Center,
            color = LightGray21
        )
    }
}

private fun drawThread(scope: DrawScope, x: Float, yStart: Float, yEnd: Float, width: Float) {
    scope.drawLine(
        color = LightGray21,
        start = Offset(x = x, y = yStart),
        end = Offset(x = x, y = yEnd),
        strokeWidth = width
    )
}
