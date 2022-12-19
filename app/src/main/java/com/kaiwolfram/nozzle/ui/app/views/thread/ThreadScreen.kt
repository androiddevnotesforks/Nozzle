package com.kaiwolfram.nozzle.ui.app.views.thread

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.model.ThreadPosition
import com.kaiwolfram.nozzle.ui.components.PostCard
import com.kaiwolfram.nozzle.ui.components.TopBar
import com.kaiwolfram.nozzle.ui.theme.spacing


@Composable
fun ThreadScreen(
    uiState: ThreadViewModelState,
    onRefreshThreadView: () -> Unit,
    onOpenThread: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onGoBack: () -> Unit,
) {
    Column {
        TopBar(text = stringResource(id = R.string.thread), onGoBack = onGoBack)
        Column {
            ThreadedPosts(
                previous = uiState.previous,
                current = uiState.current,
                replies = uiState.replies,
                currentThreadPosition = uiState.currentThreadPosition,
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefreshThreadView,
                onOpenThread = onOpenThread,
                onNavigateToProfile = onNavigateToProfile,
            )
        }
    }
}

@Composable
private fun ThreadedPosts(
    previous: List<PostWithMeta>,
    current: PostWithMeta,
    replies: List<PostWithMeta>,
    currentThreadPosition: ThreadPosition,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onOpenThread: (String) -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh,
    ) {
        val listState = LazyListState(firstVisibleItemIndex = previous.size)
        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
            itemsIndexed(previous) { index, post ->
                PostCard(
                    post = post,
                    onOpenProfile = onNavigateToProfile,
                    onNavigateToThread = onOpenThread,
                    threadPosition = if (index == 0) ThreadPosition.START else {
                        ThreadPosition.MIDDLE
                    }
                )
            }
            item {
                PostCard(
                    modifier = Modifier
                        .background(color = Color.Yellow.copy(alpha = 0.1f)),
                    post = current,
                    onOpenProfile = onNavigateToProfile,
                    threadPosition = currentThreadPosition
                )
                Divider()
                Spacer(modifier = Modifier.height(spacing.tiny))
                Divider()
            }
            items(replies) { post ->
                PostCard(
                    post = post,
                    onOpenProfile = onNavigateToProfile,
                    onNavigateToThread = onOpenThread
                )
            }
        }
    }
}
