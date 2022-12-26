package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.kaiwolfram.nozzle.ui.theme.*

@Composable
fun PostCardList(
    posts: List<PostWithMeta>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onPrepareReply: (PostWithMeta) -> Unit,
    onNavigateToThread: (String) -> Unit,
    onNavigateToReply: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenProfile: ((String) -> Unit)? = null,
) {
    SwipeRefresh(
        modifier = modifier,
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh,
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(posts) { post ->
                PostCard(
                    post = post,
                    onLike = onLike,
                    onRepost = onRepost,
                    onOpenProfile = onOpenProfile,
                    onPrepareReply = onPrepareReply,
                    onNavigateToThread = onNavigateToThread,
                    onNavigateToReply = onNavigateToReply,
                )
            }
        }
    }
}

@Composable
fun PostCard(
    post: PostWithMeta,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onPrepareReply: (PostWithMeta) -> Unit,
    modifier: Modifier = Modifier,
    threadPosition: ThreadPosition = ThreadPosition.SINGLE,
    onOpenProfile: ((String) -> Unit)? = null,
    onNavigateToThread: ((String) -> Unit)? = null,
    onNavigateToReply: () -> Unit,
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
            PostCardActions(
                // TODO: Real counts
                numOfReplies = 0,
                numOfReposts = 0,
                numOfLikes = 0,
                post = post,
                onLike = { onLike(post.id) },
                onRepost = { onRepost(post.id) },
                onPrepareReply = onPrepareReply,
                onNavigateToReply = onNavigateToReply,
            )
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
    post: PostWithMeta,
    onLike: () -> Unit,
    onRepost: () -> Unit,
    onPrepareReply: (PostWithMeta) -> Unit,
    onNavigateToReply: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(0.85f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ReplyAction(
            numOfReplies = numOfReplies,
            postToReplyTo = post,
            onPrepareReply = onPrepareReply,
            onNavigateToReply = onNavigateToReply
        )
        RepostAction(
            numOfReposts = numOfReposts,
            isRepostedByMe = post.isRepostedByMe,
            onRepost = onRepost
        )
        LikeAction(numOfLikes = numOfLikes, isLikedByMe = post.isLikedByMe, onLike = onLike)
    }
}

@Composable
private fun ReplyAction(
    numOfReplies: Int,
    postToReplyTo: PostWithMeta,
    onPrepareReply: (PostWithMeta) -> Unit,
    onNavigateToReply: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ReplyIcon(
            modifier = Modifier
                .size(sizing.smallIcon)
                .clip(CircleShape)
                .clickable {
                    onPrepareReply(postToReplyTo)
                    onNavigateToReply()
                })
        Spacer(Modifier.width(spacing.medium))
        // TODO: Show num
        Text(
            text = numOfReplies.toString(),
            color = Color.Transparent
        )
    }
}

@Composable
private fun RepostAction(
    numOfReposts: Int,
    isRepostedByMe: Boolean,
    onRepost: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val modifier = Modifier
            .size(sizing.smallIcon)
            .clip(CircleShape)
        RepostIcon(
            modifier = if (isRepostedByMe) modifier.clickable { } else modifier.clickable { onRepost() },
            tint = if (isRepostedByMe) Green21 else colors.onBackground
        )
        Spacer(Modifier.width(spacing.medium))
        // TODO: Show num
        Text(text = numOfReposts.toString(), color = Color.Transparent)
    }
}

@Composable
private fun LikeAction(
    numOfLikes: Int,
    isLikedByMe: Boolean,
    onLike: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val modifier = Modifier
            .size(sizing.smallIcon)
            .clip(CircleShape)
        LikeIcon(
            modifier = if (isLikedByMe) modifier.clickable { } else modifier.clickable { onLike() },
            isLiked = isLikedByMe,
            tint = if (isLikedByMe) Red21 else colors.onBackground
        )
        Spacer(Modifier.width(spacing.medium))
        // TODO: Show num
        Text(text = numOfLikes.toString(), color = Color.Transparent)
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
