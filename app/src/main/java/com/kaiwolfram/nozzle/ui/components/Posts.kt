package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.kaiwolfram.nozzle.model.PostIds
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.model.RepostPreview
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
    onLoadMore: () -> Unit,
    onNavigateToThread: (PostIds) -> Unit,
    onNavigateToReply: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenProfile: ((String) -> Unit)? = null,
) {
    SwipeRefresh(
        modifier = modifier,
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh,
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
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
            item {
                LaunchedEffect(true) {
                    onLoadMore()
                }
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
    onNavigateToThread: (PostIds) -> Unit,
    onNavigateToReply: () -> Unit,
    isCurrent: Boolean = false,
    threadPosition: ThreadPosition = ThreadPosition.SINGLE,
    onOpenProfile: ((String) -> Unit)? = null,
) {
    val x = sizing.profilePicture / 2 + spacing.screenEdge
    val yTop = spacing.screenEdge
    val yBottom = sizing.profilePicture + spacing.screenEdge
    val small = spacing.small
    Row(
        modifier
            .clickable(enabled = !isCurrent) { onNavigateToThread(post.toPostIds()) }
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
        PostCardProfilePicture(
            modifier = Modifier
                .size(sizing.profilePicture)
                .clip(CircleShape),
            pictureUrl = post.pictureUrl,
            pubkey = post.pubkey,
            onOpenProfile = onOpenProfile
        )
        Spacer(Modifier.width(spacing.large))
        Column {
            PostCardProfileNameAndContent(
                post = post,
                onOpenProfile = onOpenProfile,
            )
            Spacer(Modifier.height(spacing.medium))
            RepostCardContent(
                post = post.repost,
                onOpenProfile = onOpenProfile,
                onNavigateToThread = onNavigateToThread,
            )
            Spacer(Modifier.height(spacing.medium))
            PostCardActions(
                numOfReplies = post.numOfReplies,
                numOfReposts = post.numOfReposts,
                numOfLikes = post.numOfLikes,
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
private fun PostCardProfileNameAndContent(
    post: PostWithMeta,
    onOpenProfile: ((String) -> Unit)?,
) {
    Column {
        PostCardProfileName(name = post.name, pubkey = post.pubkey, onOpenProfile = onOpenProfile)
        PostCardContentBase(replyToName = post.replyToName, content = post.content)
    }
}

@Composable
private fun RepostCardContent(
    post: RepostPreview?,
    onOpenProfile: ((String) -> Unit)?,
    onNavigateToThread: (PostIds) -> Unit,
) {
    post?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = spacing.tiny,
                    color = LightGray21,
                    shape = RoundedCornerShape(spacing.large)
                )
                .clickable {
                    onNavigateToThread(it.toPostIds())
                }
        ) {
            Column(modifier = Modifier.padding(spacing.large)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PostCardProfilePicture(
                        modifier = Modifier
                            .size(sizing.smallProfilePicture)
                            .clip(CircleShape),
                        pictureUrl = it.picture,
                        pubkey = it.pubkey,
                        onOpenProfile = onOpenProfile
                    )
                    Spacer(modifier = Modifier.width(spacing.medium))
                    PostCardProfileName(
                        name = it.name,
                        pubkey = it.pubkey,
                        onOpenProfile = onOpenProfile
                    )
                }
                PostCardContentBase(replyToName = null, content = it.content)
            }
        }
    }
}

@Composable
private fun PostCardContentBase(
    replyToName: String?,
    content: String,
) {
    replyToName?.let { ReplyingTo(name = it) }
    Spacer(Modifier.height(spacing.medium))
    Text(
        text = content,
        maxLines = 21,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun PostCardProfilePicture(
    pictureUrl: String,
    pubkey: String,
    onOpenProfile: ((String) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    ProfilePicture(
        modifier = modifier,
        pictureUrl = pictureUrl,
        pubkey = pubkey,
        onOpenProfile = if (onOpenProfile != null) {
            { onOpenProfile(pubkey) }
        } else {
            null
        }
    )
}

@Composable
private fun PostCardProfileName(
    name: String,
    pubkey: String,
    onOpenProfile: ((String) -> Unit)?
) {
    Text(
        modifier = if (onOpenProfile != null) {
            Modifier.clickable { onOpenProfile(pubkey) }
        } else {
            Modifier
        },
        text = name,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
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
        Text(text = numOfReplies.toString())
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
        Text(text = numOfReposts.toString())
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
        Text(text = numOfLikes.toString())
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
